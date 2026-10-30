package com.healthcare.automation.pages;

import com.healthcare.automation.constants.FrameworkConstants;
import com.healthcare.automation.utilities.ConfigReader;
import com.healthcare.automation.utilities.WaitUtils;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

public abstract class BasePage {

    protected final WebDriver driver;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
    }

    protected static final By NAV = By.cssSelector("nav[aria-label='Primary']");
    protected static final By PAGE_TITLE = By.cssSelector("main h1");
    protected static final By LOGOUT_BUTTON = By.xpath("//button[normalize-space()='Log out']");
    protected static final By USER_ROLE = By.cssSelector("header .text-stone-500");
    protected static final By UNAUTHORIZED_ALERT = By.cssSelector("[role='alert']");
    protected static final By ERROR_ALERT = By.cssSelector("p[role='alert']");

    public boolean isNavLinkVisible(String linkText) {
        WaitUtils.waitForVisible(driver, NAV);
        return driver.findElements(By.xpath("//nav[@aria-label='Primary']//a[normalize-space()='" + linkText + "']"))
                .stream()
                .anyMatch(WebElement::isDisplayed);
    }

    public List<String> getVisibleNavLinks() {
        WaitUtils.waitForVisible(driver, NAV);
        return driver.findElements(By.cssSelector("nav[aria-label='Primary'] a"))
                .stream()
                .map(element -> element.getText().trim())
                .collect(Collectors.toList());
    }

    public void clickNavLink(String linkText) {
        WaitUtils.click(driver, By.xpath("//nav[@aria-label='Primary']//a[normalize-space()='" + linkText + "']"));
        WaitUtils.waitForDocumentReady(driver);
    }

    public void navigateToPath(String path) {
        String base = ConfigReader.getBaseUrl();
        if (!base.endsWith("/")) {
            base = base + "/";
        }
        String normalized = path.startsWith("/") ? path.substring(1) : path;
        driver.get(base + normalized);
        WaitUtils.waitForDocumentReady(driver);
    }

    public String getPageTitleText() {
        return WaitUtils.getText(driver, PAGE_TITLE);
    }

    public String getUserRoleLabel() {
        return WaitUtils.getText(driver, USER_ROLE);
    }

    public boolean isUnauthorizedMessageDisplayed() {
        try {
            WaitUtils.createWait(driver).until(webDriver ->
                    webDriver.getPageSource().contains(FrameworkConstants.UNAUTHORIZED_MESSAGE)
                            || !webDriver.findElements(PAGE_TITLE).isEmpty());
            return driver.getPageSource().contains(FrameworkConstants.UNAUTHORIZED_MESSAGE);
        } catch (TimeoutException exception) {
            return false;
        }
    }

    public void logout() {
        WaitUtils.click(driver, LOGOUT_BUTTON);
        WaitUtils.waitForUrlContains(driver, "/login");
    }

    public boolean isLoggedIn() {
        return driver.findElements(LOGOUT_BUTTON).stream().anyMatch(WebElement::isDisplayed);
    }
}
