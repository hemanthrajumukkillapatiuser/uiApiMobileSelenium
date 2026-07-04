package com.hemanth.automation.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.hemanth.automation.stepdefinitions", "com.hemanth.automation.hooks"},
        plugin = {"pretty"}
)
public class CucumberRunner extends AbstractTestNGCucumberTests {
}
