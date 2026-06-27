package com.hemanth.automation.driver;
import org.openqa.selenium.WebDriver;


public class WebDriverFactory {

    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    private WebDriverFactory() {

    }


}
