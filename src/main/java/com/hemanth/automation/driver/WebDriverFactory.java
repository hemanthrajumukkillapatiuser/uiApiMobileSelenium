package com.hemanth.automation.driver;

public class WebDriverFactory {

    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    private WebDriverFactory() {

    }


}
