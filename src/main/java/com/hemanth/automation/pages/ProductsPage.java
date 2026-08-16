package com.hemanth.automation.pages;

import org.openqa.selenium.By;

public class ProductsPage extends BasePage {

    private final By allProductsHeader = By.xpath("//h2[contains(.,'All Products')]");
    private final By firstProductName = By.xpath("(//div[@class='productinfo text-center']/p)[1]");
    private final By firstAddToCartButton =
        By.xpath("(//div[@class='productinfo text-center']//a[contains(@class,'add-to-cart')])[1]");
    private final By viewCartModalLink = By.cssSelector(".modal-content a[href='/view_cart']");

    private final By womenDressCategoryLink = By.cssSelector("a[href='/category_products/1']");
    private final By menTshirtsCategoryLink = By.cssSelector("a[href='/category_products/3']");
    private final By continueShoppingButton = By.xpath("//button[text()='Continue Shopping']");

    public boolean isAllProductsHeaderDisplayed() {

        return isDisplayed(allProductsHeader);
    }

    public String getFirstProductName() {
        return getText(firstProductName);
    }

    public void addFirstProductToCart() {
        dismissAdOverlayIfPresent();
        jsClick(firstAddToCartButton);
    }

    public void goToCartFromModal() {
        dismissAdOverlayIfPresent();
        click(viewCartModalLink);
    }

    public void selectWomenDressCategory() {
        dismissAdOverlayIfPresent();
        jsClick(womenDressCategoryLink);
    }

    public void selectMenTshirtsCategory() {
        dismissAdOverlayIfPresent();
        jsClick(menTshirtsCategoryLink);
    }

    public boolean isCategoryPageDisplayed(String categoryTitle) {
        By categoryHeader = By.xpath("//h2[contains(.,'" + categoryTitle + "')]");
        return isDisplayed(categoryHeader);
    }

    public String getProductName(int index) {
        By productName = By.xpath("(//div[@class='productinfo text-center']/p)[" + index + "]");
        return getText(productName);
    }

    public void addProductToCart(int index) {
        dismissAdOverlayIfPresent();
        By addToCartButton = By.xpath(
            "(//div[@class='productinfo text-center']//a[contains(@class,'add-to-cart')])[" + index + "]");
        jsClick(addToCartButton);
    }

    public void clickContinueShopping() {
        click(continueShoppingButton);
    }
}