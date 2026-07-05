import { expect, Locator, Page } from "@playwright/test";
import { dismissAdOverlay } from "../utils/adOverlay";

export class CartPage {
  readonly page: Page;

  constructor(page: Page) {
    this.page = page;
  }

  productInCartLocator(productName: string): Locator {
    return this.page
      .locator("td.cart_description h4 a")
      .filter({ hasText: productName });
  }

  async verifyProductInCart(productName: string) {
    await dismissAdOverlay(this.page);
    await expect(this.productInCartLocator(productName)).toBeVisible();
  }
}
