package com.hemanth.automation.driver;

import com.hemanth.automation.config.ConfigReader;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;


public class WebDriverFactory {

    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    private WebDriverFactory() {

    }

    private static WebDriver createWebDriver() {
        String browser = ConfigReader.getProperty("browser").toLowerCase();
        boolean headless = Boolean.parseBoolean(ConfigReader.getProperty("headless"));

        if (browser.equals("chrome")) {
            ChromeOptions options = new ChromeOptions();
            if (headless) {
                options.addArguments("--headless=new");
            }
            return new ChromeDriver(options);

        } else if (browser.equals("firefox")) {
            FirefoxOptions options = new FirefoxOptions();
            if (headless) {
                options.addArguments("--headless");
            }
            return new FirefoxDriver(options);

        } else if (browser.equals("edge")) {
            EdgeOptions options = new EdgeOptions();
            if (headless) {
                options.addArguments("--headless=new");
            }
            return new EdgeDriver(options);

        } else {
            throw new IllegalArgumentException("Unsupported browser: " + browser);
        }
    }

    public static void createDriver() {

        String platform = ConfigReader.getProperty("platform").toLowerCase();
        WebDriver webDriver;

        if (platform.equals("web")) {
            webDriver = createWebDriver();
        } else if (platform.equals("mobile")) {
            webDriver = createMobileDriver();
        } else {
            throw new IllegalArgumentException("Unsupported platform: " + platform);
        }

        long implicitWait = Long.parseLong(ConfigReader.getProperty("implicit.wait"));
        webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));
        if (platform.equals("web")) {
            webDriver.manage().window().maximize();
        }

        driver.set(webDriver);
    }

    private static WebDriver createMobileDriver() {
        String serverUrl  = ConfigReader.getProperty("appium.server.url") ;
        String deviceName = ConfigReader.getProperty("android.device.name") ;
        String appPath    = ConfigReader.getProperty("android.app.path") ;
        String platform = ConfigReader.getProperty("mobile.platform");

        UiAutomator2Options options = new UiAutomator2Options();
        options.setDeviceName(deviceName);
        options.setPlatformName(platform);
        if (appPath != null && !appPath.isBlank()) {
            options.setApp(appPath);
        }
        try {
            return new AndroidDriver(new URL(serverUrl), options);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Appium server URL: " + serverUrl, e);
        }
    }

    public static WebDriver getDriver() {
        if (driver.get() == null) {
            throw new IllegalStateException("Driver not created. Call createDriver() first.");
        }
        return driver.get();
    }

    public static void quitDriver() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
        }
    }

}
