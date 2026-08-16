import { test, expect } from "@playwright/test";
import { CartPage } from "../pages/CartPage";
import { HomePage } from "../pages/HomePage";
import { ProductsPage } from "../pages/ProductsPage";

test.describe("Category Products", () => {
  test("user can add dress for women and tshirt for men", async ({ page }, testInfo) => {
    testInfo.setTimeout(90_000);
    const homePage = new HomePage(page);
    const productsPage = new ProductsPage(page);
    const cartPage = new CartPage(page);

    await homePage.openHomePage();
    await homePage.verifyHomePageVisible();

    await homePage.clickProducts();
    await productsPage.verifyAllProductsHeaderVisible();

    await productsPage.selectWomenDressCategory();
    await productsPage.verifyCategoryPageDisplayed("Women - Dress Products");

    const dressName = await productsPage.getProductName(1);
    await productsPage.addProductToCart(1);
    await productsPage.clickContinueShopping();

    await productsPage.selectMenTshirtsCategory();
    await productsPage.verifyCategoryPageDisplayed("Men - Tshirts Products");

    const tshirtName = await productsPage.getProductName(1);
    await productsPage.addProductToCart(1);
    await productsPage.goToCartFromModal();

    await cartPage.verifyProductInCart(dressName);
    await cartPage.verifyProductInCart(tshirtName);
  });
});
