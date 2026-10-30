package com.healthcare.automation.utilities;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public final class WaitUtils {

    private WaitUtils() {
    }

    public static WebDriverWait createWait(WebDriver driver) {
        return new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("explicitWait", 20)));
    }

    public static WebElement waitForVisible(WebDriver driver, By locator) {
        return createWait(driver).until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement waitForClickable(WebDriver driver, By locator) {
        return createWait(driver).until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static boolean waitForInvisible(WebDriver driver, By locator) {
        return createWait(driver).until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public static void waitForPageTitleContains(WebDriver driver, String titlePart) {
        createWait(driver).until(ExpectedConditions.titleContains(titlePart));
    }

    public static void waitForUrlContains(WebDriver driver, String fragment) {
        createWait(driver).until(ExpectedConditions.urlContains(fragment));
    }

    public static void selectByVisibleText(WebDriver driver, By locator, String text) {
        WebElement element = waitForVisible(driver, locator);
        new Select(element).selectByVisibleText(text);
    }

    public static void selectByValue(WebDriver driver, By locator, String value) {
        WebElement element = waitForVisible(driver, locator);
        new Select(element).selectByValue(value);
    }

    public static void click(WebDriver driver, By locator) {
        waitForClickable(driver, locator).click();
    }

    public static void type(WebDriver driver, By locator, String text) {
        WebElement element = waitForVisible(driver, locator);
        element.clear();
        element.sendKeys(text);
    }

    public static String getText(WebDriver driver, By locator) {
        return waitForVisible(driver, locator).getText().trim();
    }

    public static List<WebElement> waitForAllVisible(WebDriver driver, By locator) {
        return createWait(driver).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    public static void waitForDocumentReady(WebDriver driver) {
        createWait(driver).until(webDriver ->
                "complete".equals(((JavascriptExecutor) webDriver).executeScript("return document.readyState")));
    }
}
