package com.healthcare.automation.utilities;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public final class JavaScriptUtils {

    private JavaScriptUtils() {
    }

    private static JavascriptExecutor executor(WebDriver driver) {
        return (JavascriptExecutor) driver;
    }

    public static void click(WebDriver driver, WebElement element) {
        executor(driver).executeScript("arguments[0].click();", element);
    }

    public static void scrollIntoView(WebDriver driver, WebElement element) {
        executor(driver).executeScript("arguments[0].scrollIntoView({block:'center'});", element);
    }

    public static void highlight(WebDriver driver, WebElement element) {
        executor(driver).executeScript(
                "arguments[0].style.border='2px solid red'", element);
    }
}
