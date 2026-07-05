import { test } from "@playwright/test";
import { CartPage } from "../pages/CartPage";
import { HomePage } from "../pages/HomePage";
import { ProductsPage } from "../pages/ProductsPage";

test("verify products page is visible", async ({ page }) => {
  const homePage = new HomePage(page);
  const productsPage = new ProductsPage(page);

  await homePage.openHomePage();
  await homePage.verifyHomePageVisible();

  await homePage.clickProducts();
  await productsPage.verifyAllProductsHeaderVisible();
});

test("user can add first product to cart and see it in the cart", async ({
  page,
}) => {
  const homePage = new HomePage(page);
  const productsPage = new ProductsPage(page);
  const cartPage = new CartPage(page);

  await homePage.openHomePage();
  await homePage.clickProducts();
  await productsPage.verifyAllProductsHeaderVisible();

  const firstProductName = await productsPage.getFirstProductName();
  await productsPage.addFirstProductToCart();
  await productsPage.goToCartFromModal();

  await cartPage.verifyProductInCart(firstProductName);
});
