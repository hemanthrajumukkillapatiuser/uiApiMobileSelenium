package com.hemanth.automation.tests;

import com.hemanth.automation.driver.WebDriverFactory;
import com.hemanth.automation.listeners.ScreenshotListener;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

@Listeners(ScreenshotListener.class)
public class BaseTest {

    @BeforeMethod
    public void setUp() {
        WebDriverFactory.createDriver();
    }

    @AfterMethod
    public void tearDown() {
        WebDriverFactory.quitDriver();
    }

}
