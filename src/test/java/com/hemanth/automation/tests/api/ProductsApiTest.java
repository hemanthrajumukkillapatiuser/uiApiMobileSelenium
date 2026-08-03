package com.hemanth.automation.tests.api;

import com.hemanth.automation.api.ApiBase;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

import java.util.List;

import static io.restassured.RestAssured.given;

@Epic("E-Commerce")
@Feature("Products API")
public class ProductsApiTest {

    @Story("Get All Products")
    @Test
    public void getAllProducts_returnsProductsList() {
        Response response = given()
                .spec(ApiBase.requestSpec())
            .when()
                .get("/productsList");

        int responseCode = response.jsonPath().getInt("responseCode");
        List<Object> products = response.jsonPath().getList("products");

        Assert.assertEquals(responseCode, 200, "Response code should be 200");
        Assert.assertFalse(products.isEmpty(), "Products list should not be empty");
    }

    @Story("Get All Products")
    @Test
    public void postToProductsList_returnsMethodNotSupported() {
        Response response = given()
                .spec(ApiBase.requestSpec())
            .when()
                .post("/productsList");

        int responseCode = response.jsonPath().getInt("responseCode");

        Assert.assertEquals(responseCode, 405, "Response code should be 405 for unsupported method");
    }

    @Story("Get All Brands")
    @Test
    public void getAllBrands_returnsBrandsList() {
        Response response = given()
                .spec(ApiBase.requestSpec())
            .when()
                .get("/brandsList");

        int responseCode = response.jsonPath().getInt("responseCode");
        List<Object> brands = response.jsonPath().getList("brands");

        Assert.assertEquals(responseCode, 200, "Response code should be 200");
        Assert.assertFalse(brands.isEmpty(), "Brands list should not be empty");
    }

    @Story("Get All Brands")
    @Test
    public void putToBrandsList_returnsMethodNotSupported() {
        Response response = given()
                .spec(ApiBase.requestSpec())
            .when()
                .put("/brandsList");

        int responseCode = response.jsonPath().getInt("responseCode");

        Assert.assertEquals(responseCode, 405, "Response code should be 405 for unsupported method");
    }

    @Story("Search Product")
    @Test
    public void searchProduct_withValidKeyword_returnsResults() {
        Response response = given()
                .spec(ApiBase.formRequestSpec())
                .formParam("search_product", "top")
            .when()
                .post("/searchProduct");

        int responseCode = response.jsonPath().getInt("responseCode");
        List<Object> products = response.jsonPath().getList("products");

        Assert.assertEquals(responseCode, 200, "Response code should be 200");
        Assert.assertFalse(products.isEmpty(), "Search should return matching products");
    }

    @Story("Search Product")
    @Test
    public void searchProduct_withoutParam_returnsBadRequest() {
        Response response = given()
                .spec(ApiBase.formRequestSpec())
            .when()
                .post("/searchProduct");

        int responseCode = response.jsonPath().getInt("responseCode");

        Assert.assertEquals(responseCode, 400, "Response code should be 400 for missing parameter");
    }
}
