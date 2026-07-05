package com.hemanth.automation.pages;

import org.openqa.selenium.By;

public class ProductsPage extends BasePage {

    private final By allProductsHeader = By.xpath("//h2[contains(.,'All Products')]");
    private final By firstProductName = By.xpath("(//div[@class='productinfo text-center']/p)[1]");
    private final By firstAddToCartButton =
        By.xpath("(//div[@class='productinfo text-center']//a[contains(@class,'add-to-cart')])[1]");
    private final By viewCartModalLink = By.cssSelector(".modal-content a[href='/view_cart']");

    public boolean isAllProductsHeaderDisplayed() {

        return isDisplayed(allProductsHeader);
    }

    public String getFirstProductName() {
        return getText(firstProductName);
    }

    public void addFirstProductToCart() {
        jsClick(firstAddToCartButton);
    }

    public void goToCartFromModal() {
        click(viewCartModalLink);
    }
}