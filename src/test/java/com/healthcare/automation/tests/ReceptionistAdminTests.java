package com.healthcare.automation.tests;

import com.healthcare.automation.base.BaseTest;
import com.healthcare.automation.pages.ReceptionistAdminPage;
import com.healthcare.automation.utilities.CredentialManager;
import com.healthcare.automation.utilities.RetryAnalyzer;
import com.healthcare.automation.utilities.TestDataProvider;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ReceptionistAdminTests extends BaseTest {

    private ReceptionistAdminPage adminPage;

    @BeforeMethod(alwaysRun = true)
    public void loginAsAdmin() {
        adminPage = new ReceptionistAdminPage(driver);
        loginAs(CredentialManager.emailForRole("AUTH_ADMIN"), CredentialManager.passwordForRole("AUTH_ADMIN"));
        dashboardPage.waitForDashboardLoaded();
    }

    @Test(description = "Admin dashboard and full menu access", retryAnalyzer = RetryAnalyzer.class)
    public void testAdminDashboardAndMenu() {
        Assert.assertEquals(dashboardPage.getUserRoleLabel(), "Administrator");
        List<String> links = dashboardPage.getVisibleNavLinks();
        Assert.assertTrue(links.contains("Users"));
        Assert.assertTrue(links.contains("Audit log"));
        Assert.assertTrue(links.contains("Billing"));
        Assert.assertTrue(links.contains("Patients"));
    }

    @Test(dataProvider = "adminData", dataProviderClass = TestDataProvider.class,
            description = "Data-driven admin workflows")
    public void testAdminWorkflows(Map<String, String> data) {
        String action = data.get("Action");
        if ("searchUser".equals(action)) {
            adminPage.openUsersPage();
            adminPage.searchUsers(data.get("SearchQuery"));
            Assert.assertTrue(adminPage.isUsersTableDisplayed());
            return;
        }
        if ("viewAudit".equals(action)) {
            adminPage.openAuditPage();
            Assert.assertTrue(adminPage.isAuditPageDisplayed());
        }
    }

    @Test(description = "Admin can access doctors management")
    public void testDoctorsManagementAccess() {
        adminPage.openDoctorsPage();
        Assert.assertEquals(adminPage.getPageTitleText(), "Doctors");
    }

    @Test(description = "Admin can create receptionist staff account")
    public void testCreateReceptionistUser() {
        adminPage.openUsersPage();
        String unique = UUID.randomUUID().toString().substring(0, 8);
        adminPage.createStaffUser(
                "Auto",
                "Receptionist" + unique,
                "auto.rec." + unique + "@example.test",
                "DemoPass123!",
                "RECEPTIONIST");
        String message = adminPage.getSuccessMessage();
        Assert.assertFalse(message.isBlank(), "Expected user creation confirmation message");
        Assert.assertTrue(message.toLowerCase().contains("user created"), "Actual message: " + message);
    }

    @Test(description = "Admin dashboard shows billing metrics")
    public void testAdminDashboardMetrics() {
        dashboardPage.waitForDashboardLoaded();
        Assert.assertTrue(dashboardPage.isStatLabelPresent("Pending invoices"));
        Assert.assertTrue(dashboardPage.isStatLabelPresent("Registered patients"));
    }
}
