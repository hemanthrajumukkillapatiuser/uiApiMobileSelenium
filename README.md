# Robust Hybrid Automation Framework

A production-ready hybrid test automation framework that covers **UI (Selenium)**, **API (RestAssured)**, and **Mobile Web (Appium)** testing through a unified **TestNG + Cucumber BDD** harness, with **Allure** reporting. A parallel **Playwright TypeScript** suite mirrors the same UI flows.

**Target Application:** [automationexercise.com](https://automationexercise.com)

## What This Framework Does

- **Web UI Testing** — Selenium-driven browser tests using the Page Object Model. Supports Chrome, Firefox, and Edge with optional headless mode.
- **API Testing** — RestAssured-based API tests for REST endpoints. Runs independently without a browser.
- **Mobile Web Testing** — The same web UI tests run on mobile Chrome via Appium + Android Emulator, with zero locator changes.
- **BDD Testing** — Cucumber feature files describe scenarios in plain English (Gherkin). Step definitions reuse the same page objects as the TestNG tests.
- **Playwright Migration** — A TypeScript Playwright suite under `playwright-ts/` mirrors the Selenium UI flows, providing an alternative test runner.
- **Allure Reporting** — Rich HTML reports with test steps, screenshots, and categorization.
- **Screenshot on Failure** — Automatic screenshot capture when any test fails.
- **Ad-Network Blocking** — CDP-based blocking of ad interstitials that interfere with test execution on the target site.

## Tech Stack

| Concern           | Library / Tool                                | Version |
| ----------------- |-----------------------------------------------| ------- |
| Language          | Java                                          | 17      |
| Build Tool        | Maven                                         | 3.x     |
| UI Automation     | Selenium                                      | 4.43.0  |
| Test Runner       | TestNG                                        | 7.12.0  |
| BDD               | Cucumber (`cucumber-java`, `cucumber-testng`) | 7.34.3  |
| API Testing       | RestAssured                                   | 6.0.0   |
| Mobile Automation | Appium java-client                            | 10.1.1  |
| Reporting         | Allure (`allure-testng`)                      | 2.34.0  |
| AOP Weaving       | AspectJ (`allure-depedencies`)                | 1.9.22  |
| Alt UI Runner     | Playwright (`@playwright/test`)               | 1.61.1+ |

> **TestNG only** — JUnit is intentionally not used in this project.

## Prerequisites

- JDK 17
- Maven 3.x
- (Mobile testing) Android SDK, Appium, and an Android emulator — see [Mobile Web Testing Setup](#mobile-web-testing-setup)
- (Playwright) Node.js 18+ — see [Playwright TypeScript Suite](#playwright-typescript-suite)
- (Reporting) [Allure CLI](https://allurereport.org/docs/install/) to view reports locally

## Quick Start

```bash
# Build and run all tests
mvn clean install

# Run all tests
mvn test

# Run a single test class
mvn test -Dtest=ProductsTest

# Run a single test method
mvn test -Dtest=ProductsTest#testAddFirstProductToCartAndVerify

# Run the Cucumber BDD suite
mvn test -Dtest=CucumberRunner

# Run API tests only (no browser needed)
mvn test -DsuiteXmlFile=src/test/resources/testNG-api.xml

# Run mobile web tests (requires emulator + Appium running)
mvn test "-DsuiteXmlFile=src/test/resources/testNG-mobile.xml" "-Dplatform=mobile"
```

> Run Maven from the **project root** — `ConfigReader` loads `config.properties` via the relative path `src/main/resources/config.properties`.

## Configuration

All settings live in `src/main/resources/config.properties` and are read through `ConfigReader`. Any key can be overridden from the command line via `-Dkey=value`.

| Key                               | Purpose                                                         | Example            |
| --------------------------------- | --------------------------------------------------------------- | ------------------ |
| `platform`                        | `web` or `mobile` — selects Selenium vs. Appium driver          | `web`              |
| `browser`                         | Browser for web runs                                            | `chrome`           |
| `headless`                        | Run the browser headless                                        | `false`            |
| `base.url`                        | Application URL under test                                      | `https://automationexercise.com` |
| `api.base.url`                    | API base URL for RestAssured tests                              | `https://automationexercise.com/api` |
| `implicit.wait` / `explicit.wait` | Selenium wait timeouts (seconds)                                | `10` / `15`        |
| `appium.server.url`               | Appium server endpoint                                          | `http://127.0.0.1:4723` |
| `mobile.platform`                 | Mobile OS                                                       | `android`          |
| `android.device.name`             | Target device/emulator name                                     | `Pixel_7_API_34`   |
| `android.app.path`                | Path to APK (leave empty for mobile web)                        | *(empty)*          |
| `mobile.browser`                  | Browser for mobile web testing                                  | `chrome`           |
| `mobile.automation.name`          | Appium automation engine                                        | `UiAutomator2`     |

## Project Structure

```
robust-hybrid-automation-framework/
│
├── src/main/java/com/hemanth/automation/
│   ├── api/
│   │   └── ApiBase.java                  # RestAssured request spec factory (JSON + form-encoded)
│   ├── config/
│   │   └── ConfigReader.java             # Loads config.properties; getProperty() accessors
│   ├── constants/
│   │   └── FrameworkConstants.java        # Shared paths (config file, screenshots, allure results)
│   ├── driver/
│   │   └── WebDriverFactory.java         # ThreadLocal<WebDriver> lifecycle; Chrome/Firefox/Edge + Appium
│   └── pages/
│       ├── BasePage.java                 # Abstract base — wait, click, jsClick, isDisplayed, getText, ad dismissal
│       ├── HomePage.java                 # Home page — open site, navigate to products
│       ├── ProductsPage.java             # Products listing — verify header, add to cart, go to cart
│       └── CartPage.java                 # Cart — verify product appears in cart
│
├── src/main/resources/
│   └── config.properties                 # Central configuration file
│
├── src/test/java/com/hemanth/automation/
│   ├── tests/
│   │   ├── BaseTest.java                 # @BeforeMethod/@AfterMethod driver lifecycle + ScreenshotListener
│   │   ├── ConfigReaderTest.java         # Verifies config loading works
│   │   ├── ProductsTest.java             # UI tests — products page, add-to-cart flow (Allure-annotated)
│   │   └── api/
│   │       └── ProductsApiTest.java      # API tests — products, brands, search endpoints (no browser)
│   ├── listeners/
│   │   └── ScreenshotListener.java       # TestNG ITestListener — captures screenshot on failure
│   ├── hooks/
│   │   └── Hooks.java                    # Cucumber @Before/@After — driver lifecycle for BDD scenarios
│   ├── stepdefinitions/
│   │   └── ProductsStepDefinitions.java  # Cucumber step definitions using the same page objects
│   └── runners/
│       └── CucumberRunner.java           # TestNG-based Cucumber runner (AbstractTestNGCucumberTests)
│
├── src/test/resources/
│   ├── features/
│   │   └── products.feature              # Gherkin scenarios — products page, add-to-cart
│   ├── testNG-api.xml                    # Suite file for API-only runs
│   └── testNG-mobile.xml                # Suite file for mobile web runs
│
├── playwright-ts/                        # Playwright TypeScript mirror suite
│   ├── pages/
│   │   ├── HomePage.ts
│   │   ├── ProductsPage.ts
│   │   └── CartPage.ts
│   ├── tests/
│   │   └── products.spec.ts
│   ├── utils/
│   │   └── adOverlay.ts                  # Ad-network blocking + overlay dismissal
│   ├── playwright.config.ts
│   └── package.json
│
├── agents/                               # AI agent prompt files
│   ├── git-push/
│   │   └── push.prompt.md               # Commits, pushes, and optionally syncs with main
│   ├── testcase-generator/
│   │   └── generate-test.prompt.md       # Generates Selenium Java TestNG tests from a flow description
│   └── selenium-to-playwright/
│       └── convert-test.prompt.md
│
├── pom.xml                               # Maven build configuration
├── README.md
├── AGENTS.md
├── CLAUDE.md
└── .github/
    └── copilot-instructions.md
```

## How the Framework Layers Work

### Web UI Tests (Selenium + TestNG)

Tests extend `BaseTest`, which creates a browser via `WebDriverFactory` before each test and quits it after. Page objects (`HomePage`, `ProductsPage`, `CartPage`) encapsulate locators and actions. `WebDriverFactory` uses a `ThreadLocal<WebDriver>` pattern for thread-safe parallel execution.

```
BaseTest → WebDriverFactory.createDriver() → HomePage / ProductsPage / CartPage → assertions
```

### API Tests (RestAssured)

API tests live in `tests.api` and do **not** extend `BaseTest` — they don't need a browser. `ApiBase` provides shared `RequestSpecification` factories for JSON and form-encoded requests, reading the base URL from config.

```
ProductsApiTest → ApiBase.requestSpec() → RestAssured given/when/then
```

### BDD Tests (Cucumber + TestNG)

Feature files in `src/test/resources/features/` describe scenarios in Gherkin. Step definitions in `stepdefinitions/` implement them using the same page objects. `Hooks` handles driver lifecycle (mirroring `BaseTest`), and `CucumberRunner` ties it all together.

```
products.feature → ProductsStepDefinitions → HomePage / ProductsPage / CartPage
```

### Mobile Web Tests (Appium)

The same web UI tests run on mobile Chrome via Appium. `WebDriverFactory` checks the `platform` config key — when set to `mobile`, it creates an Appium `AndroidDriver` instead of a desktop browser. No locator changes needed.

```
Same test → WebDriverFactory (platform=mobile) → Appium AndroidDriver → same page objects
```

## Mobile Web Testing Setup

### One-Time Setup

1. **Android Studio** — install via `winget install Google.AndroidStudio`
2. **Android SDK** — install required components:
   ```powershell
   sdkmanager "platform-tools" "emulator" "platforms;android-34" "system-images;android-34;google_apis_playstore;x86_64"
   ```
3. **Environment variables** — set permanently (User scope):
   ```powershell
   [System.Environment]::SetEnvironmentVariable("ANDROID_HOME", "C:\Users\<you>\AppData\Local\Android\Sdk", "User")
   [System.Environment]::SetEnvironmentVariable("ANDROID_SDK_ROOT", "C:\Users\<you>\AppData\Local\Android\Sdk", "User")
   ```
   Add to PATH: `%ANDROID_HOME%\platform-tools`, `%ANDROID_HOME%\emulator`, `%ANDROID_HOME%\cmdline-tools\latest\bin`
4. **Create an AVD:**
   ```powershell
   avdmanager create avd -n "Pixel_7_API_34" -k "system-images;android-34;google_apis_playstore;x86_64" -d "pixel_7"
   ```
5. **Install Appium + UiAutomator2 driver:**
   ```powershell
   npm install -g appium
   appium driver install uiautomator2
   ```

### Running Mobile Web Tests

You need three terminals:

**Terminal 1 — Start the emulator:**
```powershell
emulator -avd Pixel_7_API_34
```

**Terminal 2 — Start Appium server:**
```powershell
appium --allow-insecure=uiautomator2:chromedriver_autodownload
```

**Terminal 3 — Run the tests:**
```powershell
mvn test "-DsuiteXmlFile=src/test/resources/testNG-mobile.xml" "-Dplatform=mobile"
```

### Verify Your Environment

```powershell
adb version              # Android Debug Bridge version
adb devices              # Should show "emulator-5554  device"
appium --version         # Should show 3.x
emulator -list-avds      # Should show "Pixel_7_API_34"
```

## Playwright TypeScript Suite

A parallel Playwright suite under `playwright-ts/` mirrors the Selenium UI flows. It uses the same Page Object Model pattern and includes its own ad-network blocking via `utils/adOverlay.ts`.

```bash
cd playwright-ts
npm install              # first time only
npm test                 # run tests (headless)
npm run test:headed      # run tests with visible browser
npm run test:ui          # open Playwright's interactive UI mode
npm run report           # open the HTML report
```

## Reporting

### Allure Reports

Test results are written to `target/allure-results/`. Generate and view reports:

```bash
mvn allure:report        # generates HTML report in target/site/allure-maven-plugin
mvn allure:serve         # builds and opens the report in a browser
allure serve target/allure-results   # using Allure CLI directly
```

### Screenshot on Failure

`ScreenshotListener` (a TestNG `ITestListener` registered on `BaseTest`) automatically captures a screenshot to `target/screenshots/` whenever a test fails. Screenshots include a timestamp in the filename.

### Playwright Reports

Playwright generates its own HTML report. Open it with:

```bash
cd playwright-ts
npm run report
```

## Documentation Files

This project includes several markdown files, each with a specific purpose:

| File | What It Does |
| ---- | ------------ |
| [`README.md`](./README.md) | This file. Explains the framework, how to set it up, and how to run tests. Start here. |
| [`AGENTS.md`](./AGENTS.md) | Instructions for AI coding assistants (Claude, Copilot, Cursor, etc.). Defines the rules any AI should follow when editing this codebase — architecture details, coding conventions, and how to extend the framework. This is the **single source of truth** for all AI agents. |
| [`CLAUDE.md`](./CLAUDE.md) | Entry point for [Claude Code](https://claude.ai/code). Points to `AGENTS.md` so Claude follows the same rules as other AI tools. |
| [`.github/copilot-instructions.md`](./.github/copilot-instructions.md) | Entry point for GitHub Copilot. Points to `AGENTS.md` so Copilot follows the same rules. |
| [`agents/git-push/push.prompt.md`](./agents/git-push/push.prompt.md) | A reusable prompt for AI-assisted git commit + push workflow. Shows changes, confirms with the user, pushes, and offers to sync with main. Also available as a Claude Code skill (`/push`). |
| [`agents/testcase-generator/generate-test.prompt.md`](./agents/testcase-generator/generate-test.prompt.md) | A reusable prompt for AI-assisted generation of Selenium Java TestNG test cases. Describe a user flow and it generates page objects + test classes following the framework's conventions. |
| [`agents/selenium-to-playwright/convert-test.prompt.md`](./agents/selenium-to-playwright/convert-test.prompt.md) | A reusable prompt for AI-assisted conversion of Selenium Java tests to Playwright TypeScript tests. Defines the step-by-step workflow: read source, plan, get approval, generate, run, fix. |

**Why multiple files?** Different AI tools look for instructions in different places (`CLAUDE.md` for Claude Code, `.github/copilot-instructions.md` for Copilot). Rather than duplicating rules, they all point to `AGENTS.md` as the single source of truth. This way, updating one file keeps all AI assistants in sync.

## What's Next

- **CI Pipeline** — GitHub Actions workflow for automated test runs on push/PR
- **Cross-Browser / Parallel Runs** — parallel `testng.xml` config and cross-browser matrix (the `ThreadLocal` driver design already supports this)
- **More Test Coverage** — additional scenarios (checkout, login/signup, negative cases)
- **Native Mobile App Testing** — APK-based testing via Appium (mobile web is already working)
