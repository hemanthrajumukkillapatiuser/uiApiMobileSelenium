# Selenium to Playwright Conversion Agent

Read `AGENTS.md` for the framework architecture, Selenium conventions, Playwright target conventions, and folder structure before starting. This prompt only covers conversion-specific rules and workflow.

## Conversion Rules

Map Selenium Java patterns to Playwright TypeScript:

```text
By.id("username")          => page.locator("#username")
By.name("email")           => page.locator("[name='email']")
By.cssSelector(".login")   => page.locator(".login")
By.xpath("//a[text()]")    => page.locator("xpath=//a[text()]")

driver.get(url)            => await page.goto(url)
driver.findElement(loc)    => page.locator(...)
element.click()            => await locator.click()
element.sendKeys("text")   => await locator.fill("text")
element.getText()          => await locator.textContent()
element.isDisplayed()      => await expect(locator).toBeVisible()

Assert.assertTrue(visible) => await expect(locator).toBeVisible()
Assert.assertEquals(a, b)  => await expect(locator).toHaveText(expected)

WebDriverWait + ExpectedConditions.visibilityOfElementLocated
=> await expect(locator).toBeVisible()

Thread.sleep()
=> Do not convert. Replace with Playwright auto-waiting or expect assertion.

TestNG @Test             => Playwright test()
@BeforeMethod            => test.beforeEach()
@AfterMethod             => test.afterEach()
```

## Migration Workflow

When asked to convert a Selenium test:

1. Read the Selenium test class.
2. Read all related Java Page Object classes.
3. Identify the test flow, locators, waits, and assertions.
4. Summarize the planned files to create/update.
5. Ask for approval before editing.
6. Create or update Playwright page objects under `playwright-ts/pages/`.
7. Create or update Playwright specs under `playwright-ts/tests/`.
8. Use `@playwright/test`, `Locator`, async/await, and `expect`.
9. Run:

```bash
cd playwright-ts
npm test
```

10. Fix TypeScript, locator, or runtime issues.
11. Keep Selenium and Playwright tests runnable independently.

## Conversion Guidelines

- Do not blindly translate line by line — preserve the original test intent.
- Prefer role, text, label, test id, or CSS locators when more stable than the Selenium XPath.
- Use XPath only when the existing Selenium XPath is already reliable or no better locator exists.
- Keep assertions in the spec or page validation methods; keep locators inside page objects.
- Do not use JavaScript click unless normal Playwright click fails due to a real UI issue.
