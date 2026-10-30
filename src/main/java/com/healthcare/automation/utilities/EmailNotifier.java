package com.healthcare.automation.utilities;

import com.healthcare.automation.constants.FrameworkConstants;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class EmailNotifier {

    private static final Logger LOG = LogManager.getLogger(EmailNotifier.class);

    private EmailNotifier() {
    }

    public static void sendExecutionSummary(ExecutionSummary summary) {
        String host = envOrBlank("SMTP_HOST");
        if (host.isBlank()) {
            LOG.warn("SMTP_HOST not configured; email notification skipped");
            return;
        }

        try {
            Session session = buildSession(host);
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(required("EMAIL_FROM")));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(required("EMAIL_TO")));
            String cc = envOrBlank("EMAIL_CC");
            if (!cc.isBlank()) {
                message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc));
            }
            message.setSubject(buildSubject(summary));

            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(buildBody(summary));

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);
            attachIfExists(multipart, FrameworkConstants.REPORTS_DIR.resolve(FrameworkConstants.EXTENT_REPORT));
            attachScreenshotsZip(multipart);

            message.setContent(multipart);
            Transport.send(message);
            LOG.info("Email notification sent successfully");
        } catch (Exception exception) {
            LOG.warn("Email notification failed: {}", exception.getMessage());
        }
    }

    private static Session buildSession(String host) {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", envOrDefault("SMTP_PORT", "587"));
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        String username = required("SMTP_USERNAME");
        String password = required("SMTP_PASSWORD");
        return Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    private static void attachIfExists(Multipart multipart, Path file) throws Exception {
        if (!Files.exists(file)) {
            return;
        }
        MimeBodyPart attachment = new MimeBodyPart();
        attachment.attachFile(file.toFile());
        multipart.addBodyPart(attachment);
    }

    private static void attachScreenshotsZip(Multipart multipart) throws Exception {
        if (!Files.isDirectory(FrameworkConstants.SCREENSHOTS_DIR)) {
            return;
        }
        List<Path> screenshots;
        try (Stream<Path> stream = Files.list(FrameworkConstants.SCREENSHOTS_DIR)) {
            screenshots = stream.filter(path -> path.toString().endsWith(".png")).toList();
        }
        if (screenshots.isEmpty()) {
            return;
        }
        Path zipPath = FrameworkConstants.REPORTS_DIR.resolve("failure-screenshots.zip");
        Files.createDirectories(FrameworkConstants.REPORTS_DIR);
        try (ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            for (Path screenshot : screenshots) {
                zip.putNextEntry(new ZipEntry(screenshot.getFileName().toString()));
                Files.copy(screenshot, zip);
                zip.closeEntry();
            }
        }
        attachIfExists(multipart, zipPath);
    }

    private static String buildSubject(ExecutionSummary summary) {
        return "[Healthcare Automation] " + summary.status() + " | Passed: "
                + summary.passed() + " Failed: " + summary.failed();
    }

    private static String buildBody(ExecutionSummary summary) {
        List<String> lines = new ArrayList<>();
        lines.add("Healthcare Management Automation Execution Summary");
        lines.add("Status: " + summary.status());
        lines.add("Environment: " + summary.environment());
        lines.add("Browser: " + summary.browser());
        lines.add("Total: " + summary.total());
        lines.add("Passed: " + summary.passed());
        lines.add("Failed: " + summary.failed());
        lines.add("Skipped: " + summary.skipped());
        lines.add("Duration: " + summary.duration());
        if (summary.buildUrl() != null && !summary.buildUrl().isBlank()) {
            lines.add("Build URL: " + summary.buildUrl());
        }
        if (summary.buildNumber() != null && !summary.buildNumber().isBlank()) {
            lines.add("Build Number: " + summary.buildNumber());
        }
        return String.join(System.lineSeparator(), lines);
    }

    private static String envOrBlank(String key) {
        String value = System.getenv(key);
        return value == null ? "" : value.trim();
    }

    private static String envOrDefault(String key, String defaultValue) {
        String value = envOrBlank(key);
        return value.isBlank() ? defaultValue : value;
    }

    private static String required(String key) {
        String value = envOrBlank(key);
        if (value.isBlank()) {
            throw new IllegalStateException("Missing required email configuration: " + key);
        }
        return value;
    }

    public record ExecutionSummary(
            String status,
            String environment,
            String browser,
            int total,
            int passed,
            int failed,
            int skipped,
            String duration,
            String buildNumber,
            String buildUrl
    ) {
    }
}
