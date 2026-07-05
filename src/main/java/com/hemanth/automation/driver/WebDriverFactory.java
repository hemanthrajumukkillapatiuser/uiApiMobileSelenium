package com.hemanth.automation.driver;

import com.hemanth.automation.config.ConfigReader;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v147.network.Network;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Optional;


public class WebDriverFactory {

    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    private WebDriverFactory() {

    }

    private static WebDriver createWebDriver() {
        String browser = ConfigReader.getProperty("browser").toLowerCase();
        boolean headless = Boolean.parseBoolean(ConfigReader.getProperty("headless"));

        if (browser.equals("chrome")) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--disable-extensions");
            options.addArguments("--incognito");
            if (headless) {
                options.addArguments("--headless=new");
            }
            WebDriver chromeDriver = new ChromeDriver(options);
            blockAdNetworkRequests(chromeDriver);
            return chromeDriver;

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
            WebDriver edgeDriver = new EdgeDriver(options);
            blockAdNetworkRequests(edgeDriver);
            return edgeDriver;

        } else {
            throw new IllegalArgumentException("Unsupported browser: " + browser);
        }
    }

    /**
     * automationexercise.com serves Google-network ad creatives (including
     * full-page vignette interstitials) that intermittently intercept clicks
     * and hide page content. Blocking the ad domains at the network level via
     * CDP is more reliable than reactively dismissing whatever overlay renders.
     */
    private static void blockAdNetworkRequests(WebDriver webDriver) {
        if (!(webDriver instanceof HasDevTools)) {
            return;
        }
        try {
            DevTools devTools = ((HasDevTools) webDriver).getDevTools();
            devTools.createSession();
            devTools.send(Network.enable(
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
            devTools.send(Network.setBlockedURLs(Optional.empty(), Optional.of(List.of(
                "*doubleclick.net*",
                "*googlesyndication.com*",
                "*googleadservices.com*",
                "*adservice.google.com*"
            ))));
        } catch (Exception e) {
            // DevTools ad blocking is best-effort; continue without it if unsupported.
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
