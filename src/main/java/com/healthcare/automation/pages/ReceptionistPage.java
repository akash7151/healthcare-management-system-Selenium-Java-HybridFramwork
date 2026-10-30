package com.healthcare.automation.pages;

import com.healthcare.automation.utilities.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class ReceptionistPage extends BasePage {

    private static final By REGISTER_PATIENT_LINK = By.linkText("Register patient");
    private static final By SEARCH_INPUT = By.cssSelector("form input[placeholder*='Search']");
    private static final By SEARCH_BUTTON = By.xpath("//form//button[normalize-space()='Search']");
    private static final By PATIENTS_TABLE = By.cssSelector("table");
    private static final By BOOK_APPOINTMENT_LINK = By.linkText("Book appointment");
    private static final By FIRST_NAME = By.xpath("//label[span[normalize-space()='First name']]/input");
    private static final By LAST_NAME = By.xpath("//label[span[normalize-space()='Last name']]/input");
    private static final By DATE_OF_BIRTH = By.xpath("//label[span[normalize-space()='Date of birth']]/input");
    private static final By PHONE = By.xpath("//label[span[normalize-space()='Phone']]/input");
    private static final By SAVE_PATIENT = By.xpath("//button[normalize-space()='Save patient' or normalize-space()='Saving…']");
    private static final By PATIENT_SELECT = By.xpath("//label[span[normalize-space()='Patient']]/select");
    private static final By DOCTOR_SELECT = By.xpath("//label[span[normalize-space()='Doctor']]/select");
    private static final By APPOINTMENT_DATE = By.xpath("//label[span[normalize-space()='Date']]/input");
    private static final By REASON = By.xpath("//label[span[normalize-space()='Reason']]/input");
    private static final By SAVE_APPOINTMENT = By.xpath("//button[normalize-space()='Book appointment' or normalize-space()='Booking…']");

    public ReceptionistPage(WebDriver driver) {
        super(driver);
    }

    public void openPatientsPage() {
        clickNavLink("Patients");
        WaitUtils.waitForVisible(driver, By.xpath("//h1[normalize-space()='Patients']"));
    }

    public void openAppointmentsPage() {
        clickNavLink("Appointments");
        WaitUtils.waitForVisible(driver, By.xpath("//h1[normalize-space()='Appointments']"));
    }

    public void clickRegisterPatient() {
        WaitUtils.click(driver, REGISTER_PATIENT_LINK);
        WaitUtils.waitForVisible(driver, By.xpath("//h1[normalize-space()='Register patient']"));
    }

    public void searchPatients(String query) {
        WaitUtils.type(driver, SEARCH_INPUT, query);
        WaitUtils.click(driver, SEARCH_BUTTON);
        WaitUtils.waitForDocumentReady(driver);
    }

    public boolean isPatientsTableDisplayed() {
        return !driver.findElements(PATIENTS_TABLE).isEmpty();
    }

    public int getPatientRowCount() {
        List<WebElement> rows = driver.findElements(By.cssSelector("table tbody tr"));
        return rows.size();
    }

    public void registerPatient(String firstName, String lastName, String dob, String phone) {
        clickRegisterPatient();
        WaitUtils.type(driver, FIRST_NAME, firstName);
        WaitUtils.type(driver, LAST_NAME, lastName);
        WaitUtils.type(driver, DATE_OF_BIRTH, dob);
        WaitUtils.type(driver, PHONE, phone);
        WaitUtils.click(driver, SAVE_PATIENT);
        WaitUtils.waitForUrlContains(driver, "/patients/");
        WaitUtils.waitForDocumentReady(driver);
        WaitUtils.waitForVisible(driver, PAGE_TITLE);
    }

    public String getFormErrorMessage() {
        if (driver.findElements(ERROR_ALERT).isEmpty()) {
            return "";
        }
        return WaitUtils.getText(driver, ERROR_ALERT);
    }

    public void clickBookAppointment() {
        WaitUtils.click(driver, BOOK_APPOINTMENT_LINK);
        WaitUtils.waitForVisible(driver, By.xpath("//h1[normalize-space()='Book appointment']"));
    }

    public boolean isBookAppointmentVisible() {
        return !driver.findElements(BOOK_APPOINTMENT_LINK).isEmpty();
    }

    public void bookAppointment(String patientLabelContains, String doctorLabelContains, String date, String reason) {
        clickBookAppointment();
        selectOptionContaining(PATIENT_SELECT, patientLabelContains);
        selectOptionContaining(DOCTOR_SELECT, doctorLabelContains);
        WaitUtils.type(driver, APPOINTMENT_DATE, date);
        WaitUtils.waitForDocumentReady(driver);
        selectFirstAvailableSlot();
        WaitUtils.type(driver, REASON, reason);
        WaitUtils.click(driver, SAVE_APPOINTMENT);
        WaitUtils.waitForDocumentReady(driver);
    }

    private void selectOptionContaining(By selectLocator, String textFragment) {
        WebElement select = WaitUtils.waitForVisible(driver, selectLocator);
        List<WebElement> options = select.findElements(By.tagName("option"));
        for (WebElement option : options) {
            if (option.getText().contains(textFragment) && !option.getAttribute("value").isBlank()) {
                option.click();
                return;
            }
        }
        throw new IllegalStateException("No option containing '" + textFragment + "' found");
    }

    private void selectFirstAvailableSlot() {
        List<WebElement> slots = driver.findElements(By.cssSelector("input[type='radio'][name='slot']:not(:disabled)"));
        if (slots.isEmpty()) {
            throw new IllegalStateException("No available appointment slots for selected doctor and date");
        }
        slots.getFirst().click();
    }

    public void openFirstPatientDetails() {
        WaitUtils.click(driver, By.xpath("//table//a[normalize-space()='View']"));
        WaitUtils.waitForDocumentReady(driver);
    }
}
