package com.healthcare.automation.tests;

import com.healthcare.automation.base.BaseTest;
import com.healthcare.automation.pages.ReceptionistPage;
import com.healthcare.automation.utilities.CredentialManager;
import com.healthcare.automation.utilities.RetryAnalyzer;
import com.healthcare.automation.utilities.TestDataProvider;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ReceptionistTests extends BaseTest {

    private ReceptionistPage receptionistPage;

    @BeforeMethod(alwaysRun = true)
    public void loginAsReceptionist() {
        receptionistPage = new ReceptionistPage(driver);
        loginAs(CredentialManager.emailForRole("AUTH_RECEPTIONIST"), CredentialManager.passwordForRole("AUTH_RECEPTIONIST"));
        dashboardPage.waitForDashboardLoaded();
    }

    @Test(description = "Receptionist dashboard loads with expected navigation", retryAnalyzer = RetryAnalyzer.class)
    public void testReceptionistDashboardAndMenu() {
        Assert.assertEquals(dashboardPage.getUserRoleLabel(), "Receptionist");
        List<String> links = dashboardPage.getVisibleNavLinks();
        Assert.assertTrue(links.contains("Patients"));
        Assert.assertTrue(links.contains("Appointments"));
        Assert.assertFalse(links.contains("Billing"), "Billing menu should be hidden for receptionist");
        Assert.assertFalse(links.contains("Users"), "Users menu should be hidden for receptionist");
    }

    @Test(dataProvider = "receptionistData", dataProviderClass = TestDataProvider.class,
            description = "Data-driven receptionist workflows")
    public void testReceptionistWorkflows(Map<String, String> data) {
        String action = data.get("Action");
        if ("searchPatient".equals(action)) {
            receptionistPage.openPatientsPage();
            receptionistPage.searchPatients(data.get("SearchQuery"));
            Assert.assertTrue(receptionistPage.isPatientsTableDisplayed());
            Assert.assertTrue(receptionistPage.getPatientRowCount() > 0, "Patient search should return results");
            receptionistPage.openFirstPatientDetails();
            Assert.assertFalse(receptionistPage.getPageTitleText().isBlank());
            return;
        }
        if ("registerPatient".equals(action)) {
            String unique = UUID.randomUUID().toString().substring(0, 6);
            receptionistPage.openPatientsPage();
            String phoneSuffix = String.format("%08d", Math.abs(unique.hashCode()) % 100_000_000L);
            receptionistPage.registerPatient(
                    data.get("FirstName") + unique,
                    data.get("LastName"),
                    data.get("DOB"),
                    "98" + phoneSuffix);
            Assert.assertTrue(driver.getCurrentUrl().contains("/patients/"), "Patient detail URL should open");
            Assert.assertFalse(receptionistPage.getPageTitleText().isBlank(), "Patient detail page should open");
        }
    }

    @Test(description = "Receptionist can access appointments list and book appointment link")
    public void testAppointmentManagementAccess() {
        receptionistPage.openAppointmentsPage();
        Assert.assertEquals(receptionistPage.getPageTitleText(), "Appointments");
        Assert.assertTrue(receptionistPage.isBookAppointmentVisible());
    }

    @Test(description = "Receptionist cannot access billing module directly")
    public void testUnauthorizedBillingAccess() {
        receptionistPage.navigateToPath("/billing");
        Assert.assertTrue(receptionistPage.isUnauthorizedMessageDisplayed());
    }

    @Test(description = "Receptionist cannot access admin users page")
    public void testUnauthorizedAdminAccess() {
        receptionistPage.navigateToPath("/users");
        Assert.assertTrue(receptionistPage.isUnauthorizedMessageDisplayed());
    }

    @Test(description = "Book appointment with available slot", enabled = false)
    public void testBookAppointment() {
        receptionistPage.openAppointmentsPage();
        LocalDate date = LocalDate.now().plusDays(1);
        while (date.getDayOfWeek().getValue() > 5) {
            date = date.plusDays(1);
        }
        receptionistPage.bookAppointment("P-10001", "Kapoor", date.toString(), "Automation booking");
        Assert.assertEquals(receptionistPage.getPageTitleText(), "Appointment");
    }
}
