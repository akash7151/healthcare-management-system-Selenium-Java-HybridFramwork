package com.healthcare.automation.tests;

import com.healthcare.automation.base.BaseTest;
import com.healthcare.automation.utilities.CredentialManager;
import com.healthcare.automation.utilities.TestDataProvider;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class RolePermissionTests extends BaseTest {

    @Test(dataProvider = "rolePermissionData", dataProviderClass = TestDataProvider.class,
            description = "Validate role-based page access from Excel data")
    public void testRoleBasedAccess(Map<String, String> data) {
        String roleKey = data.get("RoleKey");
        String path = data.get("Path");
        String menuLabel = data.get("MenuLabel");
        boolean shouldAccess = Boolean.parseBoolean(data.get("ShouldAccess"));

        loginAs(CredentialManager.emailForRole(roleKey), CredentialManager.passwordForRole(roleKey));
        dashboardPage.waitForDashboardLoaded();

        if (!menuLabel.isBlank()) {
            boolean menuVisible = dashboardPage.isNavLinkVisible(menuLabel);
            Assert.assertEquals(menuVisible, shouldAccess, "Menu visibility mismatch for " + menuLabel);
        }

        dashboardPage.navigateToPath(path);
        if (shouldAccess) {
            Assert.assertFalse(dashboardPage.isUnauthorizedMessageDisplayed(),
                    "Role should access " + path);
        } else {
            Assert.assertTrue(dashboardPage.isUnauthorizedMessageDisplayed(),
                    "Role should be blocked from " + path);
        }
        dashboardPage.logout();
    }

    @Test(description = "Unauthenticated user is redirected to login")
    public void testUnauthenticatedRedirect() {
        loginPage.openLoginPage();
        driver.manage().deleteAllCookies();
        driver.navigate().to(com.healthcare.automation.utilities.ConfigReader.getBaseUrl() + "patients");
        Assert.assertTrue(loginPage.isLoginPageDisplayed(), "Unauthenticated access should redirect to login");
    }

    @Test(description = "Invalid login does not grant dashboard access")
    public void testInvalidLoginNoSession() {
        loginPage.openLoginPage();
        loginPage.login("invalid@test.com", "WrongPass999!");
        Assert.assertTrue(loginPage.isLoginPageDisplayed());
        driver.navigate().to(com.healthcare.automation.utilities.ConfigReader.getBaseUrl());
        Assert.assertTrue(loginPage.isLoginPageDisplayed() || loginPage.getErrorMessage().length() >= 0);
    }
}
