package com.healthcare.automation.pages;

import com.healthcare.automation.utilities.ConfigReader;
import com.healthcare.automation.utilities.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class LoginPage extends BasePage {

    private static final By SIGN_IN_HEADING = By.xpath("//h1[normalize-space()='Sign in']");
    private static final By EMAIL_INPUT = By.cssSelector("input[type='email']");
    private static final By PASSWORD_INPUT = By.cssSelector("input[type='password']");
    private static final By SIGN_IN_BUTTON = By.xpath("//button[normalize-space()='Sign in' or normalize-space()='Signing in…']");
    private static final By RESET_PASSWORD_LINK = By.linkText("Reset password");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage openLoginPage() {
        driver.get(ConfigReader.getBaseUrl() + "login");
        WaitUtils.waitForVisible(driver, SIGN_IN_HEADING);
        return this;
    }

    public void login(String email, String password) {
        WaitUtils.type(driver, EMAIL_INPUT, email);
        WaitUtils.type(driver, PASSWORD_INPUT, password);
        WaitUtils.click(driver, SIGN_IN_BUTTON);
        WaitUtils.waitForDocumentReady(driver);
    }

    public void submitLoginForm() {
        WaitUtils.click(driver, SIGN_IN_BUTTON);
    }

    public void enterEmail(String email) {
        WaitUtils.type(driver, EMAIL_INPUT, email);
    }

    public void enterPassword(String password) {
        WaitUtils.type(driver, PASSWORD_INPUT, password);
    }

    public void clearCredentials() {
        WaitUtils.waitForVisible(driver, EMAIL_INPUT).clear();
        WaitUtils.waitForVisible(driver, PASSWORD_INPUT).clear();
    }

    public void clearEmail() {
        WebElement email = WaitUtils.waitForVisible(driver, EMAIL_INPUT);
        email.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value=''; arguments[0].dispatchEvent(new Event('input', { bubbles: true }));",
                email);
    }

    public String waitForErrorMessage() {
        WaitUtils.createWait(driver).until(d -> !d.findElements(ERROR_ALERT).isEmpty());
        return WaitUtils.getText(driver, ERROR_ALERT);
    }

    public String getErrorMessage() {
        if (driver.findElements(ERROR_ALERT).isEmpty()) {
            return "";
        }
        return WaitUtils.getText(driver, ERROR_ALERT);
    }

    public boolean isEmailValueMissing() {
        WebElement email = WaitUtils.waitForVisible(driver, EMAIL_INPUT);
        Object missing = ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("return arguments[0].validity.valueMissing;", email);
        return Boolean.TRUE.equals(missing);
    }

    public boolean isPasswordValueMissing() {
        WebElement password = WaitUtils.waitForVisible(driver, PASSWORD_INPUT);
        Object missing = ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("return arguments[0].validity.valueMissing;", password);
        return Boolean.TRUE.equals(missing);
    }

    public boolean isPasswordMasked() {
        WebElement password = WaitUtils.waitForVisible(driver, PASSWORD_INPUT);
        return "password".equalsIgnoreCase(password.getAttribute("type"));
    }

    public boolean isLoginPageDisplayed() {
        return !driver.findElements(SIGN_IN_HEADING).isEmpty();
    }

    public boolean isResetPasswordLinkVisible() {
        return !driver.findElements(RESET_PASSWORD_LINK).isEmpty();
    }
}
