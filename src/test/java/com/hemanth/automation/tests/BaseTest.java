package com.hemanth.automation.tests;

import com.hemanth.automation.driver.WebDriverFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

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
