package com.hemanth.automation.tests;

import com.hemanth.automation.pages.CartPage;
import com.hemanth.automation.pages.HomePage;
import com.hemanth.automation.pages.ProductsPage;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;

@Epic("E-Commerce")
@Feature("Category Products")
public class CategoryProductsTest extends BaseTest {

    @Test
    @Story("Add Products from Multiple Categories")
    public void userCanAddDressForWomenAndTshirtForMen() {
        HomePage home = new HomePage();
        ProductsPage products = new ProductsPage();
        CartPage cart = new CartPage();

        home.open();
        Assert.assertTrue(home.isHomepageDisplayed(), "Homepage should be displayed");

        home.goToProductsPage();
        Assert.assertTrue(products.isAllProductsHeaderDisplayed(),
            "All Products header should be displayed");

        products.selectWomenDressCategory();
        Assert.assertTrue(products.isCategoryPageDisplayed("Women - Dress Products"),
            "Women - Dress Products category page should be displayed");

        String dressName = products.getProductName(1);
        products.addProductToCart(1);
        products.clickContinueShopping();

        products.selectMenTshirtsCategory();
        Assert.assertTrue(products.isCategoryPageDisplayed("Men - Tshirts Products"),
            "Men - Tshirts Products category page should be displayed");

        String tshirtName = products.getProductName(1);
        products.addProductToCart(1);
        products.goToCartFromModal();

        Assert.assertTrue(cart.isProductInCart(dressName),
            "Dress '" + dressName + "' should be in the cart");
        Assert.assertTrue(cart.isProductInCart(tshirtName),
            "Tshirt '" + tshirtName + "' should be in the cart");
    }
}
