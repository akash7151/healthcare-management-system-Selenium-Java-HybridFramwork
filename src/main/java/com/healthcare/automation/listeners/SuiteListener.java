package com.healthcare.automation.listeners;

import com.healthcare.automation.constants.FrameworkConstants;
import com.healthcare.automation.reports.ExtentReportManager;
import com.healthcare.automation.utilities.ConfigReader;
import com.healthcare.automation.utilities.EmailNotifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;

public class SuiteListener implements ISuiteListener {

    private static final Logger LOG = LogManager.getLogger(SuiteListener.class);

    @Override
    public void onStart(ISuite suite) {
        LOG.info("Suite started: {}", suite.getName());
        try {
            java.nio.file.Files.createDirectories(FrameworkConstants.LOGS_DIR);
            java.nio.file.Files.createDirectories(FrameworkConstants.SCREENSHOTS_DIR);
            java.nio.file.Files.createDirectories(FrameworkConstants.REPORTS_DIR);
        } catch (Exception exception) {
            LOG.warn("Could not create output directories", exception);
        }
    }

    @Override
    public void onFinish(ISuite suite) {
        int passed = 0;
        int failed = 0;
        int skipped = 0;
        int total = 0;
        for (org.testng.ISuiteResult suiteResult : suite.getResults().values()) {
            ITestContext context = suiteResult.getTestContext();
            passed += context.getPassedTests().size();
            failed += context.getFailedTests().size();
            skipped += context.getSkippedTests().size();
            total += context.getAllTestMethods().length;
        }
        String status = failed > 0 ? "FAILED" : "PASSED";
        LOG.info("Suite finished status={} passed={} failed={} skipped={}", status, passed, failed, skipped);

        EmailNotifier.sendExecutionSummary(new EmailNotifier.ExecutionSummary(
                status,
                ConfigReader.get("environment", "local"),
                ConfigReader.getBrowser(),
                total,
                passed,
                failed,
                skipped,
                ExtentReportManager.executionDuration(),
                System.getenv("BUILD_NUMBER"),
                System.getenv("BUILD_URL")
        ));
    }
}
