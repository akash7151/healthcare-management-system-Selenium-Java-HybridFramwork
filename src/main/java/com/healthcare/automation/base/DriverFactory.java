package com.healthcare.automation.base;

import com.healthcare.automation.utilities.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

public final class DriverFactory {

    private static final Logger LOG = LogManager.getLogger(DriverFactory.class);
    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverFactory() {
    }

    public static WebDriver getDriver() {
        if (DRIVER.get() == null) {
            DRIVER.set(createDriver());
        }
        return DRIVER.get();
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            LOG.info("Closing browser session");
            driver.quit();
            DRIVER.remove();
        }
    }

    private static WebDriver createDriver() {
        String browser = ConfigReader.getBrowser();
        boolean headless = ConfigReader.isHeadless();
        LOG.info("Initializing WebDriver browser={} headless={}", browser, headless);

        WebDriver driver = switch (browser) {
            case "firefox" -> createFirefox(headless);
            case "edge" -> createEdge(headless);
            default -> createChrome(headless);
        };

        driver.manage().timeouts().pageLoadTimeout(
                Duration.ofSeconds(ConfigReader.getInt("pageLoadTimeout", 40)));
        int implicit = ConfigReader.getInt("implicitWait", 0);
        if (implicit > 0) {
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicit));
        }
        driver.manage().window().maximize();
        return driver;
    }

    private static WebDriver createChrome(boolean headless) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        if (headless) {
            options.addArguments("--headless=new", "--window-size=1920,1080");
        }
        options.addArguments("--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage");
        return new ChromeDriver(options);
    }

    private static WebDriver createFirefox(boolean headless) {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = new FirefoxOptions();
        if (headless) {
            options.addArguments("-headless");
        }
        return new FirefoxDriver(options);
    }

    private static WebDriver createEdge(boolean headless) {
        WebDriverManager.edgedriver().setup();
        EdgeOptions options = new EdgeOptions();
        if (headless) {
            options.addArguments("--headless=new", "--window-size=1920,1080");
        }
        return new EdgeDriver(options);
    }
}
