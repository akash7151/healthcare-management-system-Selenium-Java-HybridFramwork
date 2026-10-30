package com.healthcare.automation.listeners;

import com.healthcare.automation.base.DriverFactory;
import com.healthcare.automation.reports.ExtentReportManager;
import com.healthcare.automation.utilities.ScreenshotUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

    private static final Logger LOG = LogManager.getLogger(TestListener.class);

    @Override
    public void onStart(ITestContext context) {
        ExtentReportManager.initReport();
    }

    @Override
    public void onTestStart(ITestResult result) {
        String description = result.getMethod().getDescription();
        if (description == null || description.isBlank()) {
            description = result.getMethod().getMethodName();
        }
        ExtentReportManager.createTest(result.getMethod().getMethodName(), description);
        LOG.info("Starting test: {}", result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentReportManager.logPass("Test passed");
        LOG.info("Test passed: {}", result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        WebDriver driver = null;
        try {
            driver = DriverFactory.getDriver();
        } catch (Exception ignored) {
            // Driver may already be closed
        }
        String screenshot = driver == null ? null : ScreenshotUtils.capture(driver, result.getMethod().getMethodName());
        Throwable throwable = result.getThrowable();
        String message = throwable == null ? "Unknown failure" : throwable.getMessage();
        ExtentReportManager.logFail(message);
        if (screenshot != null) {
            ExtentReportManager.attachScreenshot(screenshot);
        }
        LOG.error("Test failed: {}", result.getMethod().getMethodName(), throwable);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentReportManager.logInfo("Test skipped: " + result.getThrowable());
        LOG.warn("Test skipped: {}", result.getMethod().getMethodName());
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentReportManager.flushReport();
    }
}
