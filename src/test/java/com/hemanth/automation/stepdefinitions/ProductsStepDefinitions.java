package com.hemanth.automation.stepdefinitions;

import com.hemanth.automation.pages.CartPage;
import com.hemanth.automation.pages.HomePage;
import com.hemanth.automation.pages.ProductsPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class ProductsStepDefinitions {

    private final HomePage home = new HomePage();
    private final ProductsPage products = new ProductsPage();
    private final CartPage cart = new CartPage();
    private String capturedProductName;

    @Given("the user is on the home page")
    public void the_user_is_on_the_home_page() {
        home.open();
    }

    @Then("the home page should be displayed")
    public void the_home_page_should_be_displayed() {
        Assert.assertTrue(home.isHomepageDisplayed(), "Home page should be displayed");
    }

    @When("the user navigates to the Products page")
    public void the_user_navigates_to_the_products_page() {
        home.goToProductsPage();
    }

    @Then("the All Products header should be displayed")
    public void the_all_products_header_should_be_displayed() {
        Assert.assertTrue(products.isAllProductsHeaderDisplayed(), "All Products header should be displayed");
    }

    @When("the user captures the name of the first product")
    public void the_user_captures_the_name_of_the_first_product() {
        capturedProductName = products.getFirstProductName();
    }

    @When("the user adds the first product to the cart")
    public void the_user_adds_the_first_product_to_the_cart() {
        products.addFirstProductToCart();
    }

    @When("the user navigates to the cart page")
    public void the_user_navigates_to_the_cart_page() {
        products.goToCartFromModal();
    }

    @Then("the captured product should be present in the cart")
    public void the_captured_product_should_be_present_in_the_cart() {
        Assert.assertTrue(cart.isProductInCart(capturedProductName),
            "Captured product '" + capturedProductName + "' should be present in the cart");
    }
}
