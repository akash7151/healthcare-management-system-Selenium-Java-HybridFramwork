package com.healthcare.automation.constants;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class FrameworkConstants {

    private FrameworkConstants() {
    }

    public static final Path PROJECT_ROOT = Paths.get(System.getProperty("user.dir"));
    public static final Path CONFIG_PATH = PROJECT_ROOT.resolve("src/test/resources/config.properties");
    public static final Path TEST_DATA_PATH = PROJECT_ROOT.resolve("src/test/resources/testdata/TestData.xlsx");
    public static final Path KEYWORD_DATA_PATH = PROJECT_ROOT.resolve("src/test/resources/keywords/testcases.xlsx");
    public static final Path CREDENTIALS_PATH = PROJECT_ROOT.resolve("credentials.properties");
    public static final Path REPORTS_DIR = PROJECT_ROOT.resolve("reports");
    public static final Path SCREENSHOTS_DIR = PROJECT_ROOT.resolve("screenshots");
    public static final Path LOGS_DIR = PROJECT_ROOT.resolve("logs");
    public static final String EXTENT_REPORT = "ExtentReport.html";
    public static final String AUTOMATION_LOG = "automation.log";

    public static final String ROLE_RECEPTIONIST = "RECEPTIONIST";
    public static final String ROLE_BILLING = "BILLING_STAFF";
    public static final String ROLE_ADMIN = "ADMIN";

    public static final String UNAUTHORIZED_MESSAGE = "Your role cannot open this page.";
}
