package com.healthcare.automation.base;

import com.healthcare.automation.pages.DashboardPage;
import com.healthcare.automation.pages.LoginPage;
import com.healthcare.automation.utilities.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public abstract class BaseTest {

    protected final Logger log = LogManager.getLogger(getClass());
    protected WebDriver driver;
    protected LoginPage loginPage;
    protected DashboardPage dashboardPage;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        driver = DriverFactory.getDriver();
        driver.get(ConfigReader.getBaseUrl());
        loginPage = new LoginPage(driver);
        dashboardPage = new DashboardPage(driver);
        log.info("Test setup complete for {}", getClass().getSimpleName());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverFactory.quitDriver();
    }

    protected void loginAs(String email, String password) {
        log.info("Logging in as {}", email);
        for (int attempt = 1; attempt <= 2; attempt++) {
            loginPage.openLoginPage();
            loginPage.login(email, password);
            if (loginPage.isLoginPageDisplayed() && !loginPage.getErrorMessage().isBlank()) {
                if (attempt == 2) {
                    throw new IllegalStateException("Login failed: " + loginPage.getErrorMessage());
                }
                continue;
            }
            try {
                dashboardPage.waitForDashboardLoaded();
                return;
            } catch (TimeoutException exception) {
                if (attempt == 2) {
                    throw exception;
                }
                log.warn("Dashboard not ready after login; retrying attempt {}", attempt + 1);
            }
        }
    }

    protected void logoutIfLoggedIn() {
        if (dashboardPage.isLoggedIn()) {
            dashboardPage.logout();
        }
    }
}
