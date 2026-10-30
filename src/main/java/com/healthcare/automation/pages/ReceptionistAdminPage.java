package com.healthcare.automation.pages;

import com.healthcare.automation.utilities.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ReceptionistAdminPage extends BasePage {

    private static final By USERS_HEADING = By.xpath("//h1[normalize-space()='Users']");
    private static final By AUDIT_HEADING = By.xpath("//h1[normalize-space()='Audit log']");
    private static final By CREATE_USER_HEADING = By.xpath("//h2[normalize-space()='Create staff account']");
    private static final By FIRST_NAME = By.xpath("//form//label[span[normalize-space()='First name']]/input");
    private static final By LAST_NAME = By.xpath("//form//label[span[normalize-space()='Last name']]/input");
    private static final By EMAIL = By.xpath("//form//label[span[normalize-space()='Email']]/input[@type='email']");
    private static final By PASSWORD = By.xpath("//form//label[span[normalize-space()='Password']]/input[@type='password']");
    private static final By ROLE_SELECT = By.xpath("//form//label[span[normalize-space()='Role']]/select");
    private static final By CREATE_USER_BUTTON = By.xpath("//form//button[normalize-space()='Create user']");
    private static final By SUCCESS_ALERT = By.cssSelector("p[role='status']");
    private static final By USERS_TABLE = By.cssSelector("table");

    public ReceptionistAdminPage(WebDriver driver) {
        super(driver);
    }

    public void openUsersPage() {
        clickNavLink("Users");
        WaitUtils.waitForVisible(driver, USERS_HEADING);
    }

    public void openAuditPage() {
        clickNavLink("Audit log");
        WaitUtils.waitForVisible(driver, AUDIT_HEADING);
    }

    public void openDoctorsPage() {
        clickNavLink("Doctors");
        WaitUtils.waitForVisible(driver, By.xpath("//h1[normalize-space()='Doctors']"));
    }

    public boolean isUsersPageDisplayed() {
        return !driver.findElements(USERS_HEADING).isEmpty();
    }

    public boolean isAuditPageDisplayed() {
        return !driver.findElements(AUDIT_HEADING).isEmpty();
    }

    public void createStaffUser(String firstName, String lastName, String email, String password, String role) {
        WaitUtils.waitForVisible(driver, CREATE_USER_HEADING);
        WaitUtils.type(driver, FIRST_NAME, firstName);
        WaitUtils.type(driver, LAST_NAME, lastName);
        WaitUtils.type(driver, EMAIL, email);
        WaitUtils.type(driver, PASSWORD, password);
        WebElement roleSelect = WaitUtils.waitForVisible(driver, ROLE_SELECT);
        roleSelect.findElements(By.tagName("option")).stream()
                .filter(option -> role.equals(option.getAttribute("value")))
                .findFirst()
                .ifPresent(WebElement::click);
        WaitUtils.click(driver, CREATE_USER_BUTTON);
        WaitUtils.createWait(driver).until(d ->
                !d.findElements(SUCCESS_ALERT).isEmpty() || !d.findElements(ERROR_ALERT).isEmpty());
    }

    public String getSuccessMessage() {
        if (driver.findElements(SUCCESS_ALERT).isEmpty()) {
            return "";
        }
        return WaitUtils.getText(driver, SUCCESS_ALERT);
    }

    public boolean isUsersTableDisplayed() {
        return !driver.findElements(USERS_TABLE).isEmpty();
    }

    public void searchUsers(String query) {
        WaitUtils.type(driver, By.cssSelector("input[placeholder*='Search name or email']"), query);
        WaitUtils.click(driver, By.xpath("//form//button[normalize-space()='Search']"));
        WaitUtils.waitForDocumentReady(driver);
    }
}
