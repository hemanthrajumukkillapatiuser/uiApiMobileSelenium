package com.hemanth.automation.tests;

import com.hemanth.automation.pages.CartPage;
import com.hemanth.automation.pages.HomePage;
import com.hemanth.automation.pages.ProductsPage;
import org.testng.Assert;
import org.testng.annotations.Test;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;


@Epic("E-Commerce")
@Feature("Products")
public class ProductsTest extends BaseTest{

    @Story("Products Page Changes")
    @Test
    public void userCanNavigateToProductsPage() {
        HomePage home = new HomePage();
        ProductsPage products = new ProductsPage();

        home.open();
        Assert.assertTrue(home.isHomepageDisplayed(), "Home page should be displayed");

        home.goToProductsPage();
        Assert.assertTrue(products.isAllProductsHeaderDisplayed(), "All Products header should be displayed");
    }

    @Story("Add Product to Cart")
    @Test
    public void userCanAddFirstProductToCartAndSeeItInCart() {
        HomePage home = new HomePage();
        ProductsPage products = new ProductsPage();
        CartPage cart = new CartPage();

        home.open();
        home.goToProductsPage();
        Assert.assertTrue(products.isAllProductsHeaderDisplayed(), "All Products header should be displayed");

        String firstProductName = products.getFirstProductName();
        products.addFirstProductToCart();
        products.goToCartFromModal();

        Assert.assertTrue(cart.isProductInCart(firstProductName),
            "Captured product '" + firstProductName + "' should be present in the cart");
    }
}
