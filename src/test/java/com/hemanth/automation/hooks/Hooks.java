package com.hemanth.automation.hooks;

import com.hemanth.automation.driver.WebDriverFactory;
import io.cucumber.java.After;
import io.cucumber.java.Before;

public class Hooks {

    @Before
    public void setUp() {
        WebDriverFactory.createDriver();
    }

    @After
    public void tearDown() {
        WebDriverFactory.quitDriver();
    }
}
