package com.healthcare.automation.keywords;

import com.healthcare.automation.constants.FrameworkConstants;
import com.healthcare.automation.utilities.ExcelUtils;
import com.healthcare.automation.utilities.WaitUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.Map;

public class KeywordEngine {

    private static final Logger LOG = LogManager.getLogger(KeywordEngine.class);
    private final WebDriver driver;

    public KeywordEngine(WebDriver driver) {
        this.driver = driver;
    }

    public void executeTestCase(String testCaseId) {
        List<Map<String, String>> steps = ExcelUtils.readSheetByTestCase(
                FrameworkConstants.KEYWORD_DATA_PATH, "Keywords", testCaseId);
        LOG.info("Executing keyword test case {} with {} steps", testCaseId, steps.size());
        for (Map<String, String> step : steps) {
            executeStep(step);
        }
    }

    private void executeStep(Map<String, String> step) {
        String keyword = step.getOrDefault("Keyword", "").trim().toUpperCase();
        String locatorType = step.getOrDefault("LocatorType", "xpath").trim();
        String locator = step.get("Locator");
        String value = resolveValue(step.getOrDefault("Value", ""));
        LOG.info("Keyword step: {} locator={}", keyword, locator);

        switch (keyword) {
            case "OPEN" -> driver.get(value);
            case "CLICK" -> WaitUtils.click(driver, resolveLocator(locatorType, locator));
            case "TYPE" -> WaitUtils.type(driver, resolveLocator(locatorType, locator), value);
            case "VERIFY_TEXT" -> {
                String actual = WaitUtils.getText(driver, resolveLocator(locatorType, locator));
                if (!actual.contains(value)) {
                    throw new AssertionError("Expected text to contain '" + value + "' but was '" + actual + "'");
                }
            }
            case "VERIFY_URL" -> {
                if (!driver.getCurrentUrl().contains(value)) {
                    throw new AssertionError("Expected URL to contain '" + value + "' but was '" + driver.getCurrentUrl() + "'");
                }
            }
            default -> throw new IllegalArgumentException("Unsupported keyword: " + keyword);
        }
    }

    private By resolveLocator(String locatorType, String locator) {
        return switch (locatorType.toLowerCase()) {
            case "id" -> By.id(locator);
            case "name" -> By.name(locator);
            case "css" -> By.cssSelector(locator);
            case "linktext" -> By.linkText(locator);
            default -> By.xpath(locator);
        };
    }

    private String resolveValue(String raw) {
        if (raw == null || !raw.contains("${")) {
            return raw == null ? "" : raw;
        }
        String resolved = raw;
        resolved = replaceToken(resolved, "${BASE_URL}", com.healthcare.automation.utilities.ConfigReader.getBaseUrl());
        resolved = replaceToken(resolved, "${AUTH_ADMIN_EMAIL}", credential("AUTH_ADMIN", true));
        resolved = replaceToken(resolved, "${AUTH_ADMIN_PASSWORD}", credential("AUTH_ADMIN", false));
        return resolved;
    }

    private String credential(String roleKey, boolean email) {
        return email
                ? com.healthcare.automation.utilities.CredentialManager.emailForRole(roleKey)
                : com.healthcare.automation.utilities.CredentialManager.passwordForRole(roleKey);
    }

    private String replaceToken(String input, String token, String value) {
        return input.replace(token, value);
    }
}
