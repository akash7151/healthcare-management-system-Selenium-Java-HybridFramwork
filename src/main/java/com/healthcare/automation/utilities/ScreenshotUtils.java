package com.healthcare.automation.utilities;

import com.healthcare.automation.constants.FrameworkConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ScreenshotUtils {

    private static final Logger LOG = LogManager.getLogger(ScreenshotUtils.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");

    private ScreenshotUtils() {
    }

    public static String capture(WebDriver driver, String testName) {
        if (!(driver instanceof TakesScreenshot screenshotDriver)) {
            LOG.warn("Driver does not support screenshots");
            return null;
        }
        try {
            Files.createDirectories(FrameworkConstants.SCREENSHOTS_DIR);
            String safeName = testName.replaceAll("[^a-zA-Z0-9-_]", "_");
            String fileName = safeName + "_" + LocalDateTime.now().format(FORMATTER) + ".png";
            Path destination = FrameworkConstants.SCREENSHOTS_DIR.resolve(fileName);
            byte[] bytes = screenshotDriver.getScreenshotAs(OutputType.BYTES);
            Files.write(destination, bytes);
            LOG.info("Screenshot saved to {}", destination.toAbsolutePath());
            return destination.toAbsolutePath().toString();
        } catch (IOException exception) {
            LOG.error("Failed to capture screenshot", exception);
            return null;
        }
    }
}
