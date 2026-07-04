# Robust Hybrid Automation Framework

A hybrid test automation framework for driving **UI (Selenium)**, **API (rest-assured)**, and **Mobile (Appium)** tests through a single **Cucumber-BDD + TestNG** harness, with **Allure** reporting.

> **Status:** Web UI path is implemented end-to-end — driver factory, page objects, a Cucumber BDD scenario (with step definitions/hooks) and an equivalent plain-TestNG test, screenshot-on-failure, and Allure reporting all wired up. API and Mobile automation are not yet implemented (the Appium driver path exists in `WebDriverFactory` but is untested).

## Tech Stack

| Concern | Library | Version |
|---|---|---|
| Language / Build | Java | 17 |
| Build tool | Maven | — |
| UI automation | Selenium | 4.43.0 |
| Test runner | TestNG | 7.12.0 |
| BDD | Cucumber (`cucumber-java`, `cucumber-testng`) | 7.34.3 |
| API testing | rest-assured | 6.0.0 |
| Mobile automation | Appium java-client | 10.1.1 |
| Reporting | Allure (`allure-testng`) | 2.34.0 |

> **TestNG only** — JUnit is intentionally not used in this project.

## Prerequisites

- JDK 17
- Maven 3.x
- (Mobile) A running Appium server and Android emulator/device for `platform=mobile` runs
- (Optional) [Allure CLI](https://allurereport.org/docs/install/) to view reports

## Getting Started

```bash
# Build and run all tests
mvn clean install

# Run all tests only
mvn test

# Run a single test class
mvn test -Dtest=ConfigReaderTest

# Run a single test method
mvn test -Dtest=ConfigReaderTest#verifyConfigReader

# Run the Cucumber suite (via its TestNG runner)
mvn test -Dtest=CucumberRunner
```

> Run Maven from the **project root** — configuration is loaded via the relative path `src/main/resources/config.properties`, so a different working directory will break it.

## Configuration

All tunable values live in `src/main/resources/config.properties` and are read through `ConfigReader` (never hardcoded elsewhere):

| Key | Purpose |
|---|---|
| `platform` | `web` or `mobile` — selects the Selenium vs. Appium driver path |
| `browser` | Target browser for web runs (`chrome`, `firefox`, `edge`) |
| `headless` | Run the browser headless (`true`/`false`) |
| `base.url` | Application under test |
| `implicit.wait` / `explicit.wait` | Selenium wait timeouts (seconds) |
| `appium.server.url` | Appium server endpoint |
| `mobile.platform` | Mobile OS (e.g. `android`) |
| `android.device.name` | Target device/emulator |
| `android.app.path` | Path to the app under test |

## Project Structure

```
src/main/java/com/hemanth/automation/
├── config/ConfigReader.java          # Loads config.properties; getProperty(...) accessors
├── driver/WebDriverFactory.java      # ThreadLocal<WebDriver> lifecycle; creates Chrome/Firefox/Edge or Appium AndroidDriver
├── constants/FrameworkConstants.java # Shared paths (config, screenshots, allure-results)
└── pages/                            # Page objects
    ├── BasePage.java                 # Shared wait/click/isDisplayed helpers
    ├── HomePage.java
    └── ProductsPage.java

src/main/resources/config.properties  # Central configuration

src/test/java/com/hemanth/automation/
├── tests/                            # Plain TestNG tests (e.g. ConfigReaderTest, ProductsTest)
│   └── BaseTest.java                 # @BeforeMethod/@AfterMethod driver lifecycle + ScreenshotListener
├── listeners/ScreenshotListener.java  # TestNG ITestListener — screenshot on test failure
├── hooks/Hooks.java                  # Cucumber @Before/@After — driver lifecycle for BDD scenarios
├── stepdefinitions/                  # Cucumber step definitions (e.g. ProductsStepDefinitions)
└── runners/CucumberRunner.java       # AbstractTestNGCucumberTests entry point for the BDD suite

src/test/resources/features/          # .feature files (Gherkin scenarios), e.g. products.feature
```

## Cucumber BDD Layer

Feature files under `src/test/resources/features/` describe scenarios in Gherkin; step definitions in `stepdefinitions/` implement them using the same page objects as the plain TestNG tests. `Hooks` creates/quits the driver around each scenario (mirroring `BaseTest`), and `CucumberRunner` (a TestNG `AbstractTestNGCucumberTests` subclass) is the entry point that picks up `features/` and glues in `stepdefinitions` + `hooks`.

## Reporting

Allure results are written to `target/allure-results/` (generated output; not committed). The `allure-maven` plugin is configured in `pom.xml`, so reports can be generated directly:

```bash
mvn allure:report      # generates target/site/allure-maven-plugin
mvn allure:serve       # builds and opens the report in a browser
```

You can also use the Allure CLI directly:

```bash
allure serve target/allure-results
```

## Failure Diagnostics

`ScreenshotListener` (a TestNG `ITestListener`, registered via `@Listeners` on `BaseTest`) captures a screenshot to `target/screenshots/` whenever a test fails.

## AI Assistant Instructions

Contributor and AI-assistant guidance lives in [`AGENTS.md`](./AGENTS.md) (the single source of truth). [`CLAUDE.md`](./CLAUDE.md) and [`.github/copilot-instructions.md`](./.github/copilot-instructions.md) point to it.
