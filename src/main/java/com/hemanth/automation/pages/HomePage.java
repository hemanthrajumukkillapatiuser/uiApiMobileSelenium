package com.hemanth.automation.pages;

import com.hemanth.automation.config.ConfigReader;
import org.openqa.selenium.By;

public class HomePage extends BasePage {

    private final By productsLink = By.xpath("//a[contains(text(),'Products')]");
    private final By siteLogo = By.xpath("//img[@alt='Website for automation practice']");

    public void open() {
        driver.get(ConfigReader.getProperty("base.url"));
        dismissAdOverlayIfPresent();
    }

    public boolean isHomepageDisplayed() {
        return isDisplayed(siteLogo);
    }

    public void goToProductsPage() {
        dismissAdOverlayIfPresent();
        jsClick(productsLink);
    }
}
