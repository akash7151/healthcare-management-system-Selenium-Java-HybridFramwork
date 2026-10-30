package com.healthcare.automation.pages;

import com.healthcare.automation.utilities.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DashboardPage extends BasePage {

    private static final By DASHBOARD_HEADING = By.xpath("//h1[normalize-space()='Dashboard']");
    private static final By SESSION_CHECK = By.xpath("//*[contains(text(),'Checking your session')]");
    private static final By LOADING_TEXT = By.xpath("//*[contains(text(),'Loading clinic figures')]");
    private static final By RECENT_ACTIVITY = By.xpath("//h2[normalize-space()='Recent activity']");
    private static final By MODULE_STATUS = By.xpath("//h2[normalize-space()='Module status']");
    private static final By STAT_CARD = By.cssSelector("main .grid.gap-3 .rounded-md");

    public DashboardPage(WebDriver driver) {
        super(driver);
    }

    public DashboardPage waitForDashboardLoaded() {
        if (!driver.findElements(SESSION_CHECK).isEmpty()) {
            WaitUtils.waitForInvisible(driver, SESSION_CHECK);
        }
        WaitUtils.waitForVisible(driver, DASHBOARD_HEADING);
        if (!driver.findElements(LOADING_TEXT).isEmpty()) {
            WaitUtils.waitForInvisible(driver, LOADING_TEXT);
        }
        return this;
    }

    public boolean isDashboardDisplayed() {
        return !driver.findElements(DASHBOARD_HEADING).isEmpty();
    }

    public int getStatCardCount() {
        waitForDashboardLoaded();
        return driver.findElements(STAT_CARD).size();
    }

    public boolean isRecentActivityVisible() {
        return !driver.findElements(RECENT_ACTIVITY).isEmpty();
    }

    public boolean isModuleStatusVisible() {
        return !driver.findElements(MODULE_STATUS).isEmpty();
    }

    public boolean isStatLabelPresent(String label) {
        waitForDashboardLoaded();
        return !driver.findElements(By.xpath("//main//p[contains(@class,'text-xs') and normalize-space()='" + label + "']")).isEmpty();
    }
}
