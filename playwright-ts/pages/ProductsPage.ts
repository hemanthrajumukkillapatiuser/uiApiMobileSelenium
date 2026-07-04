import {expect, Locator, Page} from '@playwright/test';

export class ProductsPage {
    readonly page: Page;
    readonly allProductsHeader: Locator;

    constructor(page: Page) {
        this.page = page;
        this.allProductsHeader = page.locator("xpath=//h2[contains(.,'All Products')]");
    }

    async verifyAllProductsHeaderVisible() {
        await expect(this.allProductsHeader).toBeVisible();
    }
}