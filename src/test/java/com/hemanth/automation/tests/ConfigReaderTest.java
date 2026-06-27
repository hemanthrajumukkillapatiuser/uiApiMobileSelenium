package com.hemanth.automation.tests;

import com.hemanth.automation.config.ConfigReader;
import org.testng.annotations.Test;

public class ConfigReaderTest {

    @Test
    public void verifyConfigReader() {
        String browser = ConfigReader.getProperty("browser");
        String baseUrl = ConfigReader.getProperty("base.url");

        System.out.println("Browser: " + browser);
        System.out.println("Base URL: " + baseUrl);
    }
}
