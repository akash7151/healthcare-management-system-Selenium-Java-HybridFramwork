package com.healthcare.automation.tests;

import com.healthcare.automation.base.BaseTest;
import com.healthcare.automation.pages.BillingStaffPage;
import com.healthcare.automation.utilities.CredentialManager;
import com.healthcare.automation.utilities.RetryAnalyzer;
import com.healthcare.automation.utilities.TestDataProvider;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public class BillingStaffTests extends BaseTest {

    private BillingStaffPage billingStaffPage;

    @BeforeMethod(alwaysRun = true)
    public void loginAsBillingStaff() {
        billingStaffPage = new BillingStaffPage(driver);
        loginAs(CredentialManager.emailForRole("AUTH_BILLING"), CredentialManager.passwordForRole("AUTH_BILLING"));
        dashboardPage.waitForDashboardLoaded();
    }

    @Test(description = "Billing staff dashboard and menu visibility", retryAnalyzer = RetryAnalyzer.class)
    public void testBillingDashboardAndMenu() {
        Assert.assertEquals(dashboardPage.getUserRoleLabel(), "Billing staff");
        List<String> links = dashboardPage.getVisibleNavLinks();
        Assert.assertTrue(links.contains("Billing"));
        Assert.assertTrue(links.contains("Reports"));
        Assert.assertFalse(links.contains("Users"));
        Assert.assertFalse(links.contains("Appointments"));
    }

    @Test(dataProvider = "billingData", dataProviderClass = TestDataProvider.class,
            description = "Data-driven billing workflows")
    public void testBillingWorkflows(Map<String, String> data) {
        String action = data.get("Action");
        if ("searchInvoice".equals(action)) {
            billingStaffPage.openBillingPage();
            billingStaffPage.searchInvoices(data.get("SearchQuery"));
            Assert.assertTrue(billingStaffPage.isBillingTableDisplayed());
            return;
        }
        if ("createInvoice".equals(action)) {
            billingStaffPage.openBillingPage();
            billingStaffPage.createInvoice("P-10001", data.get("Description"), data.get("UnitPrice"));
            Assert.assertTrue(driver.getCurrentUrl().contains("/billing/"), "Invoice detail URL should open");
            Assert.assertFalse(billingStaffPage.getPageTitleText().isBlank(), "Invoice detail should open");
        }
    }

    @Test(description = "Billing staff can open reports page")
    public void testBillingReportsAccess() {
        billingStaffPage.openReportsPage();
        Assert.assertTrue(billingStaffPage.isReportsPageDisplayed());
    }

    @Test(description = "Billing staff cannot access user management")
    public void testUnauthorizedUsersAccess() {
        billingStaffPage.navigateToPath("/users");
        Assert.assertTrue(billingStaffPage.isUnauthorizedMessageDisplayed());
    }

    @Test(description = "Billing staff cannot book appointments")
    public void testUnauthorizedAppointmentBooking() {
        billingStaffPage.navigateToPath("/appointments/new");
        Assert.assertTrue(billingStaffPage.isUnauthorizedMessageDisplayed());
    }

    @Test(description = "Record payment on open invoice")
    public void testRecordPayment() {
        billingStaffPage.openBillingPage();
        billingStaffPage.createInvoice("P-10001", "Payment test " + System.currentTimeMillis(), "500.00");
        String outstanding = billingStaffPage.getOutstandingBalance();
        billingStaffPage.recordPayment(outstanding);
        Assert.assertTrue(billingStaffPage.getSuccessMessage().contains("Payment recorded"),
                "Payment success message expected");
    }
}
