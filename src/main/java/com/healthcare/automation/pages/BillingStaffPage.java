package com.healthcare.automation.pages;

import com.healthcare.automation.utilities.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class BillingStaffPage extends BasePage {

    private static final By CREATE_INVOICE_LINK = By.linkText("Create invoice");
    private static final By SEARCH_INPUT = By.cssSelector("input[placeholder*='Search invoice']");
    private static final By SEARCH_BUTTON = By.xpath("//form//button[normalize-space()='Search']");
    private static final By BILLING_TABLE = By.cssSelector("table");
    private static final By PATIENT_SELECT = By.xpath("//label[span[normalize-space()='Patient']]/select");
    private static final By DESCRIPTION = By.xpath("//label[span[normalize-space()='Description']]/input");
    private static final By UNIT_PRICE = By.xpath("//label[span[normalize-space()='Unit price (INR)']]/input");
    private static final By CREATE_INVOICE_BUTTON = By.xpath("//button[normalize-space()='Create invoice']");
    private static final By PAYMENT_AMOUNT = By.xpath("//label[span[normalize-space()='Amount']]/input");
    private static final By RECORD_PAYMENT = By.xpath("//button[normalize-space()='Record payment' or normalize-space()='Recording…']");
    private static final By SUCCESS_ALERT = By.cssSelector("p[role='status']");

    public BillingStaffPage(WebDriver driver) {
        super(driver);
    }

    public void openBillingPage() {
        clickNavLink("Billing");
        WaitUtils.waitForVisible(driver, By.xpath("//h1[normalize-space()='Billing']"));
    }

    public void openReportsPage() {
        clickNavLink("Reports");
        WaitUtils.waitForVisible(driver, By.xpath("//h1[normalize-space()='Reports']"));
    }

    public boolean isCreateInvoiceVisible() {
        return !driver.findElements(CREATE_INVOICE_LINK).isEmpty();
    }

    public void searchInvoices(String query) {
        WaitUtils.type(driver, SEARCH_INPUT, query);
        WaitUtils.click(driver, SEARCH_BUTTON);
        WaitUtils.waitForDocumentReady(driver);
    }

    public boolean isBillingTableDisplayed() {
        return !driver.findElements(BILLING_TABLE).isEmpty();
    }

    public void createInvoice(String patientLabelContains, String description, String unitPrice) {
        WaitUtils.click(driver, CREATE_INVOICE_LINK);
        WaitUtils.waitForVisible(driver, By.xpath("//h1[normalize-space()='Create invoice']"));
        selectFirstPatientMatching(patientLabelContains);
        WaitUtils.type(driver, DESCRIPTION, description);
        WaitUtils.type(driver, UNIT_PRICE, unitPrice);
        WaitUtils.click(driver, CREATE_INVOICE_BUTTON);
        WaitUtils.waitForUrlContains(driver, "/billing/");
        WaitUtils.waitForDocumentReady(driver);
        WaitUtils.waitForVisible(driver, PAGE_TITLE);
    }

    private void selectFirstPatientMatching(String fragment) {
        WebElement select = WaitUtils.waitForVisible(driver, PATIENT_SELECT);
        for (WebElement option : select.findElements(By.tagName("option"))) {
            if (!option.getAttribute("value").isBlank() && option.getText().contains(fragment)) {
                option.click();
                return;
            }
        }
        for (WebElement option : select.findElements(By.tagName("option"))) {
            if (!option.getAttribute("value").isBlank()) {
                option.click();
                return;
            }
        }
    }

    public void openFirstInvoice() {
        WaitUtils.click(driver, By.xpath("//table//a[normalize-space()='Open']"));
        WaitUtils.waitForDocumentReady(driver);
    }

    public void recordPayment(String amount) {
        WaitUtils.type(driver, PAYMENT_AMOUNT, amount);
        WaitUtils.click(driver, RECORD_PAYMENT);
        WaitUtils.createWait(driver).until(d ->
                !d.findElements(SUCCESS_ALERT).isEmpty() || !d.findElements(ERROR_ALERT).isEmpty());
    }

    public String getSuccessMessage() {
        List<WebElement> alerts = driver.findElements(SUCCESS_ALERT);
        return alerts.isEmpty() ? "" : alerts.getFirst().getText().trim();
    }

    public String getOutstandingBalance() {
        return WaitUtils.getText(driver, By.xpath("//dt[normalize-space()='Outstanding']/following-sibling::dd[1]"))
                .replace("INR", "").trim();
    }

    public String getInvoiceStatusText() {
        return WaitUtils.getText(driver, By.cssSelector("main p.text-stone-600"));
    }

    public boolean isReportsPageDisplayed() {
        return !driver.findElements(By.xpath("//h1[normalize-space()='Reports']")).isEmpty();
    }
}
