# AGENTS.md

Shared, tool-agnostic instructions for any AI coding assistant working in this repository (Claude Code, GitHub Copilot, Codex, Cursor, etc.). This is the single source of truth — other tools' instruction files point here.

## Operating Mindset

Approach every task in this repository as a **senior automation architect**. That means:

- Think about framework design, not just making a single test pass — favor reusability, maintainability, and clean separation of concerns (config, drivers, page objects, tests).
- Keep the design thread-safe and parallel-ready (e.g. the `ThreadLocal` driver pattern); don't introduce static/shared mutable state that breaks parallel runs.
- Prefer config-driven behavior over hardcoded values, and consistent patterns over one-off solutions.
- Call out design trade-offs, scalability concerns, and flakiness risks proactively rather than silently picking an approach.
- Never make changes directly. Always confirm the planned changes and get user approval before editing files.

## Overview

A **hybrid test automation framework** intended to drive UI (Selenium), API (rest-assured), and Mobile (Appium) tests through a single Cucumber-BDD + TestNG harness, with Allure reporting. The web UI path is implemented end-to-end: config loading, the `WebDriverFactory` driver lifecycle (Chrome/Firefox/Edge + an Appium `AndroidDriver` path, with CDP-based ad-network request blocking on Chrome/Edge), page objects (`HomePage`, `ProductsPage`, `CartPage`), Cucumber BDD scenarios (feature file + step definitions + hooks + runner) with equivalent plain-TestNG tests covering products-page navigation and the add-to-cart-and-verify flow, screenshot-on-failure, and Allure reporting. A parallel Playwright TypeScript migration under `playwright-ts/` mirrors the same page objects and flows. API testing and a real Mobile run against a device/emulator are not yet implemented.

## Build & Test Commands

Maven project, Java 17.

```bash
mvn clean install          # build + run tests
mvn test                   # run all tests (TestNG via Surefire)
mvn test -Dtest=ConfigReaderTest                       # single test class
mvn test -Dtest=ConfigReaderTest#verifyConfigReader    # single test method
mvn test -Dtest=CucumberRunner                         # run the Cucumber BDD suite
```

- Run Maven from the **project root** — `ConfigReader` loads `config.properties` via the relative path `src/main/resources/config.properties`, so a different working directory breaks it.
- Allure results are written under `target/allure-results/` (see `FrameworkConstants`). The `allure-maven` plugin **is** configured in `pom.xml`: `mvn allure:report` (generates `target/site/allure-maven-plugin`) or `mvn allure:serve`. You can also use the Allure CLI directly: `allure serve target/allure-results`.

## Selenium Java to Playwright TypeScript Migration Agent

This repository supports AI-assisted migration from Selenium Java tests to Playwright TypeScript tests.

### Goal

Convert existing Selenium Java TestNG or Cucumber tests into runnable Playwright TypeScript end-to-end tests while preserving the same business flow, page object design, assertions, and test intent.

### Source Framework

The source automation framework is Selenium Java.

Rules for Java code:

- Use TestNG only.
- Never introduce JUnit.
- Keep Selenium Java framework config-driven using `ConfigReader` and `config.properties`.
- Keep WebDriver logic centralized in driver/factory classes.
- Keep Selenium locators inside Java Page Object classes.
- Keep test flow inside Java test classes or Cucumber step definitions.

### Target Framework

The target automation framework is Playwright TypeScript.

Rules for Playwright code:

- Use `@playwright/test`.
- Use TypeScript.
- Use async/await.
- Use Playwright Page Object Model.
- Keep page-specific locators inside `playwright-ts/pages`.
- Keep test flow inside `playwright-ts/tests`.
- Use Playwright `Locator`.
- Use Playwright `expect` assertions.
- Do not use hard waits.
- Prefer Playwright auto-waiting and web-first assertions.
- Do not use JavaScript click unless normal Playwright click fails due to a real UI issue.

### Folder Structure

Generated Playwright files must go here:

```text
playwright-ts/
  pages/
  tests/
  utils/
  playwright.config.ts
  package.json
```

### Conversion Rules

Use these rules when converting Selenium Java code to Playwright TypeScript:

```text
By.id("username")
=> page.locator("#username")

By.name("email")
=> page.locator("[name='email']")

By.cssSelector(".login")
=> page.locator(".login")

By.xpath("//a[contains(text(),'Products')]")
=> page.locator("xpath=//a[contains(text(),'Products')]")

driver.get(url)
=> await page.goto(url)

driver.findElement(locator)
=> page.locator(...)

element.click()
=> await locator.click()

element.sendKeys("text")
=> await locator.fill("text")

element.getText()
=> await locator.textContent()

element.isDisplayed()
=> await expect(locator).toBeVisible()

Assert.assertTrue(pageObject.isElementVisible())
=> await expect(locator).toBeVisible()

Assert.assertEquals(actual, expected)
=> await expect(locator).toHaveText(expected)

WebDriverWait + ExpectedConditions.visibilityOfElementLocated
=> await expect(locator).toBeVisible()

Thread.sleep()
=> Do not convert. Replace with Playwright auto-waiting, locator wait, or expect assertion.

TestNG @Test
=> Playwright test()

@BeforeMethod
=> test.beforeEach()

@AfterMethod
=> test.afterEach()
```

### Migration Workflow

When asked to convert a Selenium test to Playwright:

1. Read the Selenium test class.
2. Read the related Java Page Object classes.
3. Identify the test flow, locators, waits, and assertions.
4. Create or update matching Playwright page objects under `playwright-ts/pages`.
5. Create or update matching Playwright specs under `playwright-ts/tests`.
6. Use `@playwright/test`, `Locator`, async/await, and `expect`.
7. Run the Playwright tests.
8. Fix TypeScript, locator, or runtime issues.
9. Keep Selenium and Playwright runnable independently.
10. Do not blindly translate line by line.
11. Preserve the original test intent.
12. Prefer role, text, label, test id, or CSS locators when they are more stable.
13. Use XPath only when the existing Selenium XPath is already reliable or no better locator exists.
14. Keep assertions in the spec or page validation methods, but keep locators inside page objects.
15. Do not use JavaScript click unless normal Playwright click fails due to a real UI issue.

### Commands

Run Selenium Java tests:

```bash
mvn clean test
```

Run Playwright TypeScript tests:

```bash
cd playwright-ts
npm test
```

Run Playwright headed:

```bash
cd playwright-ts
npm run test:headed
```

Open Playwright report:

```bash
cd playwright-ts
npm run report
```

## Architecture

Single Maven module under `com.hemanth.automation`:

- **`config.ConfigReader`** — central, app-wide configuration access. Loads `src/main/resources/config.properties` once in a static initializer and exposes `getProperty(key)` / `getProperty(key, default)`. All tunable values (browser, headless, base URL, waits, platform, Appium settings) flow through here; **do not hardcode** these values elsewhere.
- **`driver.WebDriverFactory`** — owns the `WebDriver` lifecycle via a `ThreadLocal<WebDriver>`, so the design is thread-safe for parallel execution. `createDriver()` reads `platform` and dispatches to `createWebDriver()` (Chrome/Firefox/Edge, config-driven headless) or `createMobileDriver()` (Appium `AndroidDriver` via `UiAutomator2Options`), then applies `implicit.wait` and (for web) maximizes the window. For Chrome/Edge, `createWebDriver()` also calls `blockAdNetworkRequests()`, which uses the Chrome DevTools Protocol (`Network.setBlockedURLs`) to block known ad domains (the site under test serves ad interstitials that intermittently intercept clicks); this is best-effort and silently no-ops if DevTools isn't available. `getDriver()` / `quitDriver()` round out the lifecycle, with `quitDriver()` calling `remove()` on the ThreadLocal.
- **`constants.FrameworkConstants`** — non-instantiable holder for shared paths (`CONFIG_FILE_PATH`, `SCREENSHOT_PATH`, `ALLURE_RESULTS_PATH`).
- **`pages`** — page objects. `BasePage` constructs its own `WebDriverWait` (from `explicit.wait`) and exposes `click` / `jsClick` / `isDisplayed` / `getText` helpers plus `dismissAdOverlayIfPresent()` (best-effort fallback dismissal of any ad overlay that slips past the network block); `HomePage`, `ProductsPage`, and `CartPage` extend it and hold their own locators.
- **`tests`** — plain TestNG test classes. `BaseTest` handles `@BeforeMethod`/`@AfterMethod` driver lifecycle and registers `ScreenshotListener` via `@Listeners`; `ConfigReaderTest` and `ProductsTest` (Allure-annotated with `@Epic`/`@Feature`/`@Story`) extend it or stand alone.
- **`listeners.ScreenshotListener`** — a TestNG `ITestListener` that saves a screenshot to `FrameworkConstants.SCREENSHOT_PATH` on `onTestFailure`.
- **BDD layer** — `src/test/resources/features/*.feature` (Gherkin) + `stepdefinitions/*StepDefinitions` (Cucumber glue using the same page objects as the TestNG tests) + `hooks.Hooks` (`@Before`/`@After` driver lifecycle, mirroring `BaseTest`) + `runners.CucumberRunner` (`AbstractTestNGCucumberTests` subclass wired via `@CucumberOptions` to the `features/` dir and both glue packages).

### Configuration (`src/main/resources/config.properties`)

The `platform` key (`web` / `mobile`) is the intended switch that selects between the Selenium and Appium driver paths in `WebDriverFactory`. Other keys: `browser`, `headless`, `base.url`, `implicit.wait`, `explicit.wait`, and the `appium.server.url` / `mobile.platform` / `android.device.name` / `android.app.path` group for mobile runs.

## Notes for extending the framework

- New driver logic belongs in `WebDriverFactory` and must keep the `ThreadLocal` pattern (set on creation, `remove()` on quit) to stay parallel-safe.
- New file-system or report paths belong in `FrameworkConstants`; new tunable settings belong in `config.properties` and are read through `ConfigReader`.
- New pages extend `BasePage` and keep locators private to the page class; reuse its `click`/`jsClick`/`isDisplayed` helpers instead of calling Selenium/`WebDriverWait` directly.
- New BDD scenarios: add a `.feature` file under `src/test/resources/features/`, implement steps in `stepdefinitions/`, and reuse `Hooks` for driver setup/teardown — don't duplicate lifecycle logic in new step-definition classes. New glue packages must be added to `CucumberRunner`'s `@CucumberOptions(glue = ...)`.
- `App.java` is leftover Maven-archetype boilerplate (`Hello World` main) and is not part of the framework.
