package com.hemanth.automation.tests;

import com.hemanth.automation.pages.HomePage;
import com.hemanth.automation.pages.ProductsPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ProductsTest extends BaseTest{

    @Test
    public void userCanNavigateToProductsPage() {
        HomePage home = new HomePage();
        ProductsPage products = new ProductsPage();

        home.open();
        Assert.assertTrue(home.isHomepageDisplayed(), "Home page should be displayed");

        home.goToProductsPage();
        Assert.assertTrue(products.isAllProductsHeaderDisplayed(), "All Products header should be displayed");
    }




}
