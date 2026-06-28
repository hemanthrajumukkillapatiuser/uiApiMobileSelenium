package com.hemanth.automation.pages;

import org.openqa.selenium.By;

public class ProductsPage extends BasePage {

    private final By allProductsHeader = By.xpath("//h2[text()='All Products']");

    public boolean isAllProductsHeaderDisplayed() {

        return isDisplayed(allProductsHeader);
    }
}