import { expect, Locator, Page } from "@playwright/test";
import { clickWithAdGuard, dismissAdOverlay } from "../utils/adOverlay";

export class ProductsPage {
  readonly page: Page;
  readonly allProductsHeader: Locator;
  readonly firstProductName: Locator;
  readonly firstAddToCartButton: Locator;
  readonly viewCartModalLink: Locator;

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
}
