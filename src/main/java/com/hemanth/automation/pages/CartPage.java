package com.hemanth.automation.pages;

import org.openqa.selenium.By;

public class CartPage extends BasePage {

    public boolean isProductInCart(String productName) {
        By productRow = By.xpath("//td[@class='cart_description']//h4/a[text()='" + productName + "']");
        return isDisplayed(productRow);
    }
}
