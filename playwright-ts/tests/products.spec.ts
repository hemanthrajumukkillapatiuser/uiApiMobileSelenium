import {test} from '@playwright/test';
import {HomePage} from '../pages/HomePage';
import {ProductsPage} from '../pages/ProductsPage';

test('verify products page is visible', async ({page}) => {
    const homePage = new HomePage(page);
    const productsPage = new ProductsPage(page);

    await homePage.openHomePage();
    await homePage.verifyHomePageVisible();

    await homePage.clickProducts();
    await productsPage.verifyAllProductsHeaderVisible();
});