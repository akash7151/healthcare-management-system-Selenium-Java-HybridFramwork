package com.healthcare.automation.tests;

import com.healthcare.automation.base.BaseTest;
import com.healthcare.automation.keywords.KeywordEngine;
import com.healthcare.automation.utilities.CredentialManager;
import com.healthcare.automation.utilities.RetryAnalyzer;
import com.healthcare.automation.utilities.TestDataProvider;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class LoginTests extends BaseTest {

    @Test(description = "Valid login redirects to dashboard", retryAnalyzer = RetryAnalyzer.class)
    public void testValidAdminLogin() {
        loginAs(CredentialManager.emailForRole("AUTH_ADMIN"), CredentialManager.passwordForRole("AUTH_ADMIN"));
        dashboardPage.waitForDashboardLoaded();
        Assert.assertTrue(dashboardPage.isDashboardDisplayed(), "Dashboard should be visible after login");
        Assert.assertEquals(dashboardPage.getUserRoleLabel(), "Administrator");
        dashboardPage.logout();
        Assert.assertTrue(loginPage.isLoginPageDisplayed(), "Logout should return to login page");
    }

    @Test(dataProvider = "loginData", dataProviderClass = TestDataProvider.class,
            description = "Data-driven login scenarios from Excel")
    public void testLoginScenarios(Map<String, String> data) {
        String email = resolvePlaceholder(data.get("Email"));
        String password = resolvePlaceholder(data.get("Password"));
        String expected = data.get("ExpectedResult");

        loginPage.openLoginPage();
        loginPage.login(email, password);

        if ("error".equals(expected)) {
            String error = loginPage.waitForErrorMessage();
            Assert.assertFalse(error.isBlank(), "Error message should be displayed");
            Assert.assertTrue(loginPage.isLoginPageDisplayed());
            return;
        }
        dashboardPage.waitForDashboardLoaded();
        Assert.assertTrue(dashboardPage.isDashboardDisplayed());
        dashboardPage.logout();
    }

    @Test(description = "Empty email shows HTML5 validation")
    public void testEmptyEmailValidation() {
        loginPage.openLoginPage();
        loginPage.clearEmail();
        loginPage.enterPassword("SomePassword1!");
        loginPage.submitLoginForm();
        Assert.assertTrue(loginPage.isEmailValueMissing(), "Browser validation should block empty email");
        Assert.assertTrue(loginPage.isLoginPageDisplayed());
    }

    @Test(description = "Empty password shows HTML5 validation")
    public void testEmptyPasswordValidation() {
        loginPage.openLoginPage();
        loginPage.enterEmail("test@example.com");
        loginPage.submitLoginForm();
        Assert.assertTrue(loginPage.isPasswordValueMissing(), "Browser validation should block empty password");
        Assert.assertTrue(loginPage.isLoginPageDisplayed());
    }

    @Test(description = "Both fields empty shows validation")
    public void testBothFieldsEmptyValidation() {
        loginPage.openLoginPage();
        loginPage.clearCredentials();
        loginPage.submitLoginForm();
        Assert.assertTrue(loginPage.isLoginPageDisplayed());
    }

    @Test(description = "Password field is masked on login page")
    public void testPasswordMasking() {
        loginPage.openLoginPage();
        Assert.assertTrue(loginPage.isPasswordMasked(), "Password input should be type=password");
    }

    @Test(description = "Keyword-driven login smoke test")
    public void testKeywordDrivenLogin() {
        KeywordEngine engine = new KeywordEngine(driver);
        engine.executeTestCase("KW_LOGIN_001");
        dashboardPage.waitForDashboardLoaded();
        Assert.assertTrue(dashboardPage.isDashboardDisplayed());
        dashboardPage.logout();
    }

    @Test(description = "Browser refresh keeps session when logged in")
    public void testSessionAfterRefresh() {
        loginAs(CredentialManager.emailForRole("AUTH_ADMIN"), CredentialManager.passwordForRole("AUTH_ADMIN"));
        dashboardPage.waitForDashboardLoaded();
        driver.navigate().refresh();
        dashboardPage.waitForDashboardLoaded();
        Assert.assertTrue(dashboardPage.isLoggedIn(), "Session should persist after refresh");
        dashboardPage.logout();
    }

    private String resolvePlaceholder(String value) {
        if (value == null) {
            return "";
        }
        String trimmed = value.trim();
        if (trimmed.startsWith("${") && trimmed.endsWith("}")) {
            String key = trimmed.substring(2, trimmed.length() - 1);
            if ("AUTH_ADMIN_EMAIL".equals(key)) {
                return CredentialManager.emailForRole("AUTH_ADMIN");
            }
            if ("AUTH_ADMIN_PASSWORD".equals(key)) {
                return CredentialManager.passwordForRole("AUTH_ADMIN");
            }
            if ("AUTH_DEFAULT_PASSWORD".equals(key)) {
                return CredentialManager.passwordForRole("AUTH_RECEPTIONIST");
            }
        }
        return trimmed;
    }
}
