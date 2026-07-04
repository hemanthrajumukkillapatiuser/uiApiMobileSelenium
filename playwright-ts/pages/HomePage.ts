import { expect, Locator, Page } from '@playwright/test';

export class HomePage {
    readonly page: Page;
    readonly productsLink: Locator;
    readonly siteLogo: Locator;

    constructor(page: Page) {
        this.page = page;
        this.productsLink = page.locator("xpath=//a[contains(text(),'Products')]");
        this.siteLogo = page.locator("xpath=//img[@alt='Website for automation practice']");
    }

    async openHomePage() {
        await this.page.goto('/');
    }

    async verifyHomePageVisible() {
        await expect(this.siteLogo).toBeVisible();
    }

    async clickProducts() {
        await this.productsLink.click();
    }
}