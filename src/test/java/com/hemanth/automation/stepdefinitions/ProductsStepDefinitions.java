package com.hemanth.automation.stepdefinitions;

import com.hemanth.automation.pages.HomePage;
import com.hemanth.automation.pages.ProductsPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class ProductsStepDefinitions {

    private final HomePage home = new HomePage();
    private final ProductsPage products = new ProductsPage();

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
}
