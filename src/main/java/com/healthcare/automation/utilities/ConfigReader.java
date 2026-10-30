package com.healthcare.automation.utilities;

import com.healthcare.automation.constants.FrameworkConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Properties;

public final class ConfigReader {

    private static final Logger LOG = LogManager.getLogger(ConfigReader.class);
    private static final Properties PROPERTIES = new Properties();

    static {
        load();
    }

    private ConfigReader() {
    }

    private static void load() {
        try (InputStream stream = Files.newInputStream(FrameworkConstants.CONFIG_PATH)) {
            PROPERTIES.load(stream);
        } catch (IOException exception) {
            try (InputStream classpath = ConfigReader.class.getClassLoader().getResourceAsStream("config.properties")) {
                if (classpath != null) {
                    PROPERTIES.load(classpath);
                } else {
                    throw new IllegalStateException("config.properties not found", exception);
                }
            } catch (IOException nested) {
                throw new IllegalStateException("Unable to load config.properties", nested);
            }
        }
        overrideFromSystemProperties();
        LOG.info("Configuration loaded for environment={}", get("environment"));
    }

    private static void overrideFromSystemProperties() {
        overrideIfPresent("baseUrl");
        overrideIfPresent("browser");
        overrideIfPresent("headless");
        overrideIfPresent("implicitWait");
        overrideIfPresent("explicitWait");
        overrideIfPresent("pageLoadTimeout");
        overrideIfPresent("environment");
        overrideIfPresent("thread.count");
        overrideIfPresent("test.suite");
    }

    private static void overrideIfPresent(String key) {
        String value = System.getProperty(key);
        if (value != null && !value.isBlank()) {
            PROPERTIES.setProperty(key, value.trim());
        }
    }

    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }

    public static String get(String key, String defaultValue) {
        return PROPERTIES.getProperty(key, defaultValue);
    }

    public static int getInt(String key, int defaultValue) {
        String value = get(key);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return Integer.parseInt(value.trim());
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        String value = get(key);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value.trim());
    }

    public static String getBaseUrl() {
        return get("baseUrl", "http://localhost:5173/");
    }

    public static String getBrowser() {
        return get("browser", "chrome").toLowerCase();
    }

    public static boolean isHeadless() {
        return getBoolean("headless", false);
    }
}
