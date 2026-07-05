import { Locator, Page } from "@playwright/test";

const AD_OVERLAY_WAIT_MS = 3000;
const AD_DOMAINS = /doubleclick\.net|googlesyndication\.com|googleadservices\.com|adservice\.google\.com/;

/**
 * automationexercise.com serves Google-network ad creatives (including
 * full-page vignette interstitials) that intermittently intercept clicks and
 * hide page content. Blocking the ad domains at the network level is more
 * reliable than reactively dismissing whatever overlay renders.
 */
export async function blockAdNetwork(page: Page) {
  await page.route(AD_DOMAINS, (route) => route.abort());
}

export async function dismissAdOverlay(page: Page): Promise<boolean> {
  const closeButton = page.getByText("Close", { exact: true });
  try {
    await closeButton.waitFor({ state: "visible", timeout: AD_OVERLAY_WAIT_MS });
    await closeButton.click();
    return true;
  } catch {
    return false;
  }
}

/**
 * This site fires a full-page ad interstitial on the first click anywhere on
 * the page, which swallows that click's effect. If an overlay appears right
 * after clicking, dismiss it and repeat the click.
 */
export async function clickWithAdGuard(locator: Locator) {
  await locator.click();
  if (await dismissAdOverlay(locator.page())) {
    await locator.click();
  }
}
