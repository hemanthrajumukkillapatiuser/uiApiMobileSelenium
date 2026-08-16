import { expect, Locator, Page } from "@playwright/test";
import { clickWithAdGuard, dismissAdOverlay } from "../utils/adOverlay";

export class ProductsPage {
  readonly page: Page;
  readonly allProductsHeader: Locator;
  readonly firstProductName: Locator;
  readonly firstAddToCartButton: Locator;
  readonly viewCartModalLink: Locator;
  readonly womenPanelToggle: Locator;
  readonly womenDressCategoryLink: Locator;
  readonly menPanelToggle: Locator;
  readonly menTshirtsCategoryLink: Locator;
  readonly continueShoppingButton: Locator;

  constructor(page: Page) {
    this.page = page;
    this.allProductsHeader = page.locator(
      "xpath=//h2[contains(.,'All Products')]",
    );
    this.firstProductName = page.locator(".productinfo.text-center p").first();
    this.firstAddToCartButton = page
      .locator(".productinfo.text-center a.add-to-cart")
      .first();
    this.viewCartModalLink = page.locator(
      ".modal-content a[href='/view_cart']",
    );
    this.womenPanelToggle = page.locator("a[href='#Women']");
    this.womenDressCategoryLink = page.locator(
      "a[href='/category_products/1']",
    );
    this.menPanelToggle = page.locator("a[href='#Men']");
    this.menTshirtsCategoryLink = page.locator(
      "a[href='/category_products/3']",
    );
    this.continueShoppingButton = page.getByRole("button", {
      name: "Continue Shopping",
    });
  }

  async verifyAllProductsHeaderVisible() {
    await dismissAdOverlay(this.page);
    await expect(this.allProductsHeader).toBeVisible();
  }

  async getFirstProductName(): Promise<string> {
    return (await this.firstProductName.textContent()) ?? "";
  }

  async addFirstProductToCart() {
    await clickWithAdGuard(this.firstAddToCartButton);
  }

  async goToCartFromModal() {
    await clickWithAdGuard(this.viewCartModalLink);
  }

  async selectWomenDressCategory() {
    await clickWithAdGuard(this.womenPanelToggle);
    await clickWithAdGuard(this.womenDressCategoryLink);
  }

  async selectMenTshirtsCategory() {
    await clickWithAdGuard(this.menPanelToggle);
    await clickWithAdGuard(this.menTshirtsCategoryLink);
  }

  async verifyCategoryPageDisplayed(categoryTitle: string) {
    await expect(
      this.page.locator(`xpath=//h2[contains(.,'${categoryTitle}')]`),
    ).toBeVisible();
  }

  async getProductName(index: number): Promise<string> {
    return (
      (await this.page
        .locator(".productinfo.text-center p")
        .nth(index - 1)
        .textContent()) ?? ""
    );
  }

  async addProductToCart(index: number) {
    await clickWithAdGuard(
      this.page.locator(".productinfo.text-center a.add-to-cart").nth(index - 1),
    );
  }

  async clickContinueShopping() {
    await this.continueShoppingButton.click();
  }
}
