package com.hemanth.automation.listeners;

import com.hemanth.automation.constants.FrameworkConstants;
import com.hemanth.automation.driver.WebDriverFactory;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenshotListener implements ITestListener {

    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String fileName = testName + "_" + timestamp + ".png";

        Path screenshotDir = Paths.get(FrameworkConstants.SCREENSHOT_PATH);

        try {
            Files.createDirectories(screenshotDir);
        } catch (IOException e) {
            System.err.println("[ScreenshotListener] Could not create screenshot directory: " + e.getMessage());
            return;
        }

        try {
            TakesScreenshot camera = (TakesScreenshot) WebDriverFactory.getDriver();
            File tempFile = camera.getScreenshotAs(OutputType.FILE);
            Path destination = screenshotDir.resolve(fileName);
            Files.copy(tempFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("[ScreenshotListener] Screenshot saved: " + destination.toAbsolutePath());
        } catch (IllegalStateException e) {
            System.err.println("[ScreenshotListener] No driver available — skipping screenshot for: " + testName);
        } catch (IOException e) {
            System.err.println("[ScreenshotListener] Failed to save screenshot for " + testName + ": " + e.getMessage());
        }
    }
}