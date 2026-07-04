# AGENTS.md

Shared, tool-agnostic instructions for any AI coding assistant working in this repository (Claude Code, GitHub Copilot, Codex, Cursor, etc.). This is the single source of truth — other tools' instruction files point here.

## Operating Mindset

Approach every task in this repository as a **senior automation architect**. That means:

- Think about framework design, not just making a single test pass — favor reusability, maintainability, and clean separation of concerns (config, drivers, page objects, tests).
- Keep the design thread-safe and parallel-ready (e.g. the `ThreadLocal` driver pattern); don't introduce static/shared mutable state that breaks parallel runs.
- Prefer config-driven behavior over hardcoded values, and consistent patterns over one-off solutions.
- Call out design trade-offs, scalability concerns, and flakiness risks proactively rather than silently picking an approach.
- Never makes changes directly always confirm before making any changes and get it approved by user.

## Overview

A **hybrid test automation framework** intended to drive UI (Selenium), API (rest-assured), and Mobile (Appium) tests through a single Cucumber-BDD + TestNG harness, with Allure reporting. The web UI path is implemented end-to-end: config loading, the `WebDriverFactory` driver lifecycle (Chrome/Firefox/Edge + an Appium `AndroidDriver` path), page objects, a Cucumber BDD scenario (feature file + step definitions + hooks + runner), an equivalent plain-TestNG test, screenshot-on-failure, and Allure reporting. API testing and a real Mobile run against a device/emulator are not yet implemented.

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

## Test Framework Convention

**TestNG only — do not use JUnit.** JUnit was deliberately removed from this project (there is no JUnit dependency in `pom.xml`). All tests use `org.testng.annotations.*`. The Cucumber integration is `cucumber-testng`, not `cucumber-junit`.

## Architecture

Single Maven module under `com.hemanth.automation`:

- **`config.ConfigReader`** — central, app-wide configuration access. Loads `src/main/resources/config.properties` once in a static initializer and exposes `getProperty(key)` / `getProperty(key, default)`. All tunable values (browser, headless, base URL, waits, platform, Appium settings) flow through here; **do not hardcode** these values elsewhere.
- **`driver.WebDriverFactory`** — owns the `WebDriver` lifecycle via a `ThreadLocal<WebDriver>`, so the design is thread-safe for parallel execution. `createDriver()` reads `platform` and dispatches to `createWebDriver()` (Chrome/Firefox/Edge, config-driven headless) or `createMobileDriver()` (Appium `AndroidDriver` via `UiAutomator2Options`), then applies `implicit.wait` and (for web) maximizes the window. `getDriver()` / `quitDriver()` round out the lifecycle, with `quitDriver()` calling `remove()` on the ThreadLocal.
- **`constants.FrameworkConstants`** — non-instantiable holder for shared paths (`CONFIG_FILE_PATH`, `SCREENSHOT_PATH`, `ALLURE_RESULTS_PATH`).
- **`pages`** — page objects. `BasePage` constructs its own `WebDriverWait` (from `explicit.wait`) and exposes `click` / `jsClick` / `isDisplayed` helpers; `HomePage` and `ProductsPage` extend it and hold their own locators.
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
