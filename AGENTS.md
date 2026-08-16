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

A **hybrid test automation framework** driving UI (Selenium), API (RestAssured), and Mobile (Appium) tests through a single Cucumber-BDD + TestNG harness, with Allure reporting. All three test layers are implemented: config loading via `ConfigReader`, the `WebDriverFactory` driver lifecycle (Chrome/Firefox/Edge + Appium `AndroidDriver`, with CDP-based ad-network request blocking), page objects (`HomePage`, `ProductsPage`, `CartPage`), `ApiBase` for RestAssured request specs (JSON + form-encoded), Cucumber BDD scenarios with equivalent plain-TestNG tests, screenshot-on-failure, and Allure reporting. API tests live in `tests.api` and run without a browser. A parallel Playwright TypeScript migration under `playwright-ts/` mirrors the same UI flows.

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

## Git Push Agent

Commits changes to the current branch, pushes to remote, and optionally pulls latest from main. The agent prompt lives at `agents/git-push/push.prompt.md`. A Claude Code skill (`/push`) is also available at `.claude/skills/push.md`.

### What it does

1. Shows `git status` and the current branch.
2. Shows the diff and summarizes changes.
3. Checks for secrets, IDE files, or build artifacts — warns before committing.
4. Asks for confirmation and a commit message.
5. Commits and pushes to the remote.
6. Reports the commit hash, branch, and remote URL.
7. Offers to pull latest from main and push again.

### Usage

Claude Code (slash command):
```
/push
```

Any AI tool (read the prompt):
```
Read agents/git-push/push.prompt.md and run it.
```

---

## Test Case Generator Agent

This repository supports AI-assisted generation of Selenium Java TestNG test cases. The agent prompt lives at `agents/testcase-generator/generate-test.prompt.md`.

### What it does

Given a user flow description (e.g., "test the login page" or "verify checkout works"), the agent:

1. Reads existing page objects to see what's already available.
2. Plans which page objects to create/update and which test class to generate.
3. Asks for approval before editing files.
4. Generates page objects (extending `BasePage`) and test classes (extending `BaseTest`) following the framework's established patterns — TestNG, Allure annotations, config-driven, thread-safe.
5. Compiles and runs the new test, fixing any issues.

### Usage

Point any AI coding assistant at the prompt file and describe the flow you want tested:

```
Read agents/testcase-generator/generate-test.prompt.md, then generate a test for the login flow.
```

The agent generates UI tests by default. It can also generate API tests (using `ApiBase`, no `BaseTest`) or BDD scenarios (feature files + step definitions) when asked.

---

## Playwright TypeScript Suite

A parallel Playwright TypeScript test suite under `playwright-ts/` mirrors the same UI flows as the Selenium Java tests.

### Conventions

- Use `@playwright/test` and TypeScript with async/await.
- Use Playwright `Locator` and `expect` assertions.
- Page-specific locators go in `playwright-ts/pages/`.
- Test flow goes in `playwright-ts/tests/`.
- Prefer Playwright auto-waiting and web-first assertions — no hard waits.
- Prefer role, text, label, test id, or CSS locators when more stable than XPath.
- Do not use JavaScript click unless normal Playwright click fails due to a real UI issue.

### Folder Structure

```text
playwright-ts/
  pages/
  tests/
  utils/
  playwright.config.ts
  package.json
```

### Commands

```bash
cd playwright-ts
npm test              # run tests
npm run test:headed   # run headed
npm run report        # open Playwright report
```

---

## Selenium Java to Playwright TypeScript Migration Agent

Converts Selenium Java tests into Playwright TypeScript while preserving test intent, business flow, and assertions. The conversion-specific rules and workflow live in the agent prompt at `agents/selenium-to-playwright/convert-test.prompt.md` — they are not loaded unless you explicitly invoke the agent.

### Usage

Point any AI coding assistant at the prompt file and specify which test to convert:

```
Read agents/selenium-to-playwright/convert-test.prompt.md, then convert ProductsTest to Playwright.
```

## Architecture

Single Maven module under `com.hemanth.automation`:

- **`config.ConfigReader`** — central, app-wide configuration access. Loads `src/main/resources/config.properties` once in a static initializer and exposes `getProperty(key)` / `getProperty(key, default)`. All tunable values (browser, headless, base URL, waits, platform, Appium settings) flow through here; **do not hardcode** these values elsewhere.
- **`driver.WebDriverFactory`** — owns the `WebDriver` lifecycle via a `ThreadLocal<WebDriver>`, so the design is thread-safe for parallel execution. `createDriver()` reads `platform` and dispatches to `createWebDriver()` (Chrome/Firefox/Edge, config-driven headless) or `createMobileDriver()` (Appium `AndroidDriver` via `UiAutomator2Options`), then applies `implicit.wait` and (for web) maximizes the window. For Chrome/Edge, `createWebDriver()` also calls `blockAdNetworkRequests()`, which uses the Chrome DevTools Protocol (`Network.setBlockedURLs`) to block known ad domains (the site under test serves ad interstitials that intermittently intercept clicks); this is best-effort and silently no-ops if DevTools isn't available. `getDriver()` / `quitDriver()` round out the lifecycle, with `quitDriver()` calling `remove()` on the ThreadLocal.
- **`constants.FrameworkConstants`** — non-instantiable holder for shared paths (`CONFIG_FILE_PATH`, `SCREENSHOT_PATH`, `ALLURE_RESULTS_PATH`).
- **`pages`** — page objects. `BasePage` constructs its own `WebDriverWait` (from `explicit.wait`) and exposes `click` / `jsClick` / `isDisplayed` / `getText` helpers plus `dismissAdOverlayIfPresent()` (best-effort fallback dismissal of any ad overlay that slips past the network block); `HomePage`, `ProductsPage`, and `CartPage` extend it and hold their own locators.
- **`api.ApiBase`** — provides shared, immutable `static final` `RequestSpecification` instances for API tests: `requestSpec()` (JSON content type) and `formRequestSpec()` (URL-encoded form content type). Both read `api.base.url` from config. Thread-safe for parallel runs because `given().spec(...)` copies the spec per request.
- **`tests`** — plain TestNG test classes. `BaseTest` handles `@BeforeMethod`/`@AfterMethod` driver lifecycle and registers `ScreenshotListener` via `@Listeners`; `ConfigReaderTest` and `ProductsTest` (Allure-annotated with `@Epic`/`@Feature`/`@Story`) extend it or stand alone.
- **`tests.api`** — API test classes that do **not** extend `BaseTest` (no browser needed). `ProductsApiTest` covers the `/productsList`, `/brandsList`, and `/searchProduct` endpoints with happy-path and negative tests. Runnable independently via `testNG-api.xml`.
- **`listeners.ScreenshotListener`** — a TestNG `ITestListener` that saves a screenshot to `FrameworkConstants.SCREENSHOT_PATH` on `onTestFailure`.
- **BDD layer** — `src/test/resources/features/*.feature` (Gherkin) + `stepdefinitions/*StepDefinitions` (Cucumber glue using the same page objects as the TestNG tests) + `hooks.Hooks` (`@Before`/`@After` driver lifecycle, mirroring `BaseTest`) + `runners.CucumberRunner` (`AbstractTestNGCucumberTests` subclass wired via `@CucumberOptions` to the `features/` dir and both glue packages).

### Configuration (`src/main/resources/config.properties`)

The `platform` key (`web` / `mobile`) is the intended switch that selects between the Selenium and Appium driver paths in `WebDriverFactory`. Other keys: `browser`, `headless`, `base.url`, `api.base.url`, `implicit.wait`, `explicit.wait`, and the `appium.server.url` / `mobile.platform` / `android.device.name` / `android.app.path` group for mobile runs.

## Notes for extending the framework

- New driver logic belongs in `WebDriverFactory` and must keep the `ThreadLocal` pattern (set on creation, `remove()` on quit) to stay parallel-safe.
- New file-system or report paths belong in `FrameworkConstants`; new tunable settings belong in `config.properties` and are read through `ConfigReader`.
- New pages extend `BasePage` and keep locators private to the page class; reuse its `click`/`jsClick`/`isDisplayed` helpers instead of calling Selenium/`WebDriverWait` directly.
- New BDD scenarios: add a `.feature` file under `src/test/resources/features/`, implement steps in `stepdefinitions/`, and reuse `Hooks` for driver setup/teardown — don't duplicate lifecycle logic in new step-definition classes. New glue packages must be added to `CucumberRunner`'s `@CucumberOptions(glue = ...)`.
- New API tests go in the `tests.api` package and must **not** extend `BaseTest`. Use `ApiBase.requestSpec()` for JSON endpoints and `ApiBase.formRequestSpec()` for form-param endpoints. Add new test classes to `testNG-api.xml`.
