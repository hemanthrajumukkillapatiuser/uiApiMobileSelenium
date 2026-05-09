package com.hemanth.automation.driver;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    private static final Properties prop = new Properties();

    static {
        try {
            FileInputStream file = new FileInputStream("src/main/resources/config.properties");
            prop.load(file);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load config.properties file", e);
        }

    }

    private ConfigReader() {
    }

    public static String getProperty(String key) {
        return prop.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return prop.getProperty(key, defaultValue);
    }
}

