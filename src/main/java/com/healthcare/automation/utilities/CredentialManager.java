package com.healthcare.automation.utilities;

import com.healthcare.automation.constants.FrameworkConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Properties;

public final class CredentialManager {

    private static final Logger LOG = LogManager.getLogger(CredentialManager.class);
    private static final Properties CREDENTIALS = new Properties();

    static {
        loadCredentials();
    }

    private CredentialManager() {
    }

    private static void loadCredentials() {
        if (Files.exists(FrameworkConstants.CREDENTIALS_PATH)) {
            try (InputStream stream = Files.newInputStream(FrameworkConstants.CREDENTIALS_PATH)) {
                CREDENTIALS.load(stream);
                LOG.info("Loaded credentials from credentials.properties");
            } catch (IOException exception) {
                LOG.warn("Could not read credentials.properties: {}", exception.getMessage());
            }
        } else {
            LOG.info("credentials.properties not found; using environment variables only");
        }
    }

    public static String emailForRole(String roleKey) {
        return resolve(roleKey + "_EMAIL", roleKey + "_USERNAME");
    }

    public static String passwordForRole(String roleKey) {
        return resolve(roleKey + "_PASSWORD", "AUTH_DEFAULT_PASSWORD");
    }

    private static String resolve(String primaryKey, String fallbackKey) {
        String envPrimary = System.getenv(primaryKey);
        if (envPrimary != null && !envPrimary.isBlank()) {
            return envPrimary.trim();
        }
        String propertyPrimary = CREDENTIALS.getProperty(primaryKey);
        if (propertyPrimary != null && !propertyPrimary.isBlank()) {
            return propertyPrimary.trim();
        }
        String envFallback = System.getenv(fallbackKey);
        if (envFallback != null && !envFallback.isBlank()) {
            return envFallback.trim();
        }
        String propertyFallback = CREDENTIALS.getProperty(fallbackKey);
        if (propertyFallback != null && !propertyFallback.isBlank()) {
            return propertyFallback.trim();
        }
        throw new IllegalStateException("Missing credential for " + primaryKey
                + ". Set environment variable or add to credentials.properties (gitignored).");
    }

    public static boolean hasCredential(String key) {
        String env = System.getenv(key);
        if (env != null && !env.isBlank()) {
            return true;
        }
        return CREDENTIALS.getProperty(key) != null && !CREDENTIALS.getProperty(key).isBlank();
    }
}
