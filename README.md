# Robust Hybrid Automation Framework

A hybrid test automation framework for driving **UI (Selenium)**, **API (rest-assured)**, and **Mobile (Appium)** tests through a single **Cucumber-BDD + TestNG** harness, with **Allure** reporting.

> **Status:** All three test layers are implemented — **Web UI** (Selenium), **API** (RestAssured), and **Mobile Web** (Appium). Config-driven `ConfigReader`, a thread-safe `WebDriverFactory` (Chrome/Firefox/Edge for web, Appium `AndroidDriver` for mobile web), page objects (`HomePage`, `ProductsPage`, `CartPage`), `ApiBase` for API request specs, and tests covered in both plain TestNG **and** Cucumber BDD. The same UI tests run on both desktop and mobile Chrome without locator changes. Screenshot-on-failure and Allure reporting are wired up. A parallel **Playwright TypeScript** suite under `playwright-ts/` mirrors the same UI flows.

## Tech Stack

| Concern           | Library                                       | Version |
| ----------------- | --------------------------------------------- | ------- |
| Language / Build  | Java                                          | 17      |
| Build tool        | Maven                                         | —       |
| UI automation     | Selenium                                      | 4.43.0  |
| Test runner       | TestNG                                        | 7.12.0  |
| BDD               | Cucumber (`cucumber-java`, `cucumber-testng`) | 7.34.3  |
| API testing       | rest-assured                                  | 6.0.0   |
| Mobile automation | Appium java-client                            | 10.1.1  |
| Reporting         | Allure (`allure-testng`)                      | 2.34.0  |

> **TestNG only** — JUnit is intentionally not used in this project.

## Prerequisites

- JDK 17
- Maven 3.x
- (Mobile) Android SDK, Appium, and an Android emulator — see [Mobile Web Testing Setup](#mobile-web-testing-setup)
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

# Run API tests only (no browser needed)
mvn test -DsuiteXmlFile=src/test/resources/testNG-api.xml
```

> Run Maven from the **project root** — configuration is loaded via the relative path `src/main/resources/config.properties`, so a different working directory will break it.

## Configuration

All tunable values live in `src/main/resources/config.properties` and are read through `ConfigReader` (never hardcoded elsewhere):

| Key                               | Purpose                                                         |
| --------------------------------- | --------------------------------------------------------------- |
| `platform`                        | `web` or `mobile` — selects the Selenium vs. Appium driver path |
| `browser`                         | Target browser for web runs (`chrome`, `firefox`, `edge`)       |
| `headless`                        | Run the browser headless (`true`/`false`)                       |
| `base.url`                        | Application under test                                          |
| `api.base.url`                    | API base URL for RestAssured tests                              |
| `implicit.wait` / `explicit.wait` | Selenium wait timeouts (seconds)                                |
| `appium.server.url`               | Appium server endpoint                                          |
| `mobile.platform`                 | Mobile OS (e.g. `android`)                                      |
| `android.device.name`             | Target device/emulator                                          |
| `android.app.path`                | Path to the app under test (leave empty for mobile web)         |
| `mobile.browser`                  | Browser for mobile web testing (`chrome`)                       |
| `mobile.automation.name`          | Appium automation engine (`UiAutomator2`)                       |

Any config key can be overridden from the command line via `-Dkey=value` (e.g. `-Dplatform=mobile`).

## Project Structure

```
src/main/java/com/hemanth/automation/
├── api/ApiBase.java                  # RestAssured RequestSpecification factory (JSON + form-encoded specs)
├── config/ConfigReader.java          # Loads config.properties; getProperty(...) accessors
├── driver/WebDriverFactory.java      # ThreadLocal<WebDriver> lifecycle; creates Chrome/Firefox/Edge or Appium AndroidDriver
├── constants/FrameworkConstants.java # Shared paths (config, screenshots, allure-results)
└── pages/                            # Page objects
    ├── BasePage.java                 # Shared wait/click/isDisplayed/getText helpers + ad-overlay dismissal
    ├── HomePage.java
    ├── ProductsPage.java
    └── CartPage.java

src/main/resources/config.properties  # Central configuration

src/test/java/com/hemanth/automation/
├── tests/                            # Plain TestNG UI tests (e.g. ConfigReaderTest, ProductsTest)
│   ├── BaseTest.java                 # @BeforeMethod/@AfterMethod driver lifecycle + ScreenshotListener
│   └── api/ProductsApiTest.java      # API tests — products, brands, search (no browser needed)
├── listeners/ScreenshotListener.java  # TestNG ITestListener — screenshot on test failure
├── hooks/Hooks.java                  # Cucumber @Before/@After — driver lifecycle for BDD scenarios
├── stepdefinitions/                  # Cucumber step definitions (e.g. ProductsStepDefinitions)
└── runners/CucumberRunner.java       # AbstractTestNGCucumberTests entry point for the BDD suite

src/test/resources/
├── features/                         # .feature files (Gherkin scenarios), e.g. products.feature
├── testNG-api.xml                    # Suite file for API-only test runs
└── testNG-mobile.xml                 # Suite file for mobile web test runs
```

## Cucumber BDD Layer

Feature files under `src/test/resources/features/` describe scenarios in Gherkin; step definitions in `stepdefinitions/` implement them using the same page objects as the plain TestNG tests. `Hooks` creates/quits the driver around each scenario (mirroring `BaseTest`), and `CucumberRunner` (a TestNG `AbstractTestNGCucumberTests` subclass) is the entry point that picks up `features/` and glues in `stepdefinitions` + `hooks`.

## Mobile Web Testing Setup

The framework runs the same web tests on mobile Chrome via Appium + Android Emulator. No locator changes needed — the page objects work on both desktop and mobile.

### One-Time Setup (already done if you followed this guide)

1. **Android Studio** — install via `winget install Google.AndroidStudio`
2. **Android SDK** — install command-line tools, platform-tools, emulator, and a system image:
   ```powershell
   sdkmanager "platform-tools" "emulator" "platforms;android-34" "system-images;android-34;google_apis_playstore;x86_64"
   ```
3. **Environment variables** — set these permanently (User scope):
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

Three terminals are needed:

**Terminal 1 — Start the emulator:**
```powershell
emulator -avd Pixel_7_API_34
```
Wait for the Android home screen to appear.

**Terminal 2 — Start Appium server:**
```powershell
appium --allow-insecure=uiautomator2:chromedriver_autodownload
```
Keep this running. You should see: `Appium REST http interface listener started on http://0.0.0.0:4723`

**Terminal 3 — Run the tests:**
```powershell
mvn test "-DsuiteXmlFile=src/test/resources/testNG-mobile.xml" "-Dplatform=mobile"
```

Chrome will open on the emulator, navigate to automationexercise.com, and execute the same test flows.

### Verifying the Environment

```powershell
adb version              # Android Debug Bridge version
adb devices              # Should show "emulator-5554  device"
appium --version         # Should show 3.x
emulator -list-avds      # Should show "Pixel_7_API_34"
```

### Switching Between Web and Mobile

```powershell
# Desktop web (default)
mvn test

# Mobile web
mvn test "-DsuiteXmlFile=src/test/resources/testNG-mobile.xml" "-Dplatform=mobile"
```

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

## Agents

This project is built to be worked on with AI coding assistants, and the agent behavior is version-controlled alongside the code. There are three layers:

### 1. Instruction agents (how any assistant should behave)

These files define the operating rules for any AI assistant editing the repo — act as a **senior automation architect**, keep everything config-driven, TestNG only, thread-safe parallel design, and confirm changes before editing.

| File                                                                 | Role                                                              |
| -------------------------------------------------------------------- | ---------------------------------------------------------------- |
| [`AGENTS.md`](./AGENTS.md)                                           | **Single source of truth** — mindset, commands, architecture, migration rules |
| [`CLAUDE.md`](./CLAUDE.md)                                           | Claude Code entry point — points to `AGENTS.md`                  |
| [`.github/copilot-instructions.md`](./.github/copilot-instructions.md) | GitHub Copilot entry point — points to `AGENTS.md`              |

### 2. Selenium → Playwright migration agent

A repo-owned prompt agent that converts Selenium Java tests into equivalent Playwright TypeScript tests while preserving the business flow, page-object design, and assertions.

- Prompt: [`agents/selenium-to-playwright/convert-test.prompt.md`](./agents/selenium-to-playwright/convert-test.prompt.md)
- Conversion rules & locator mappings: the *"Selenium Java to Playwright TypeScript Migration Agent"* section of [`AGENTS.md`](./AGENTS.md)
- Workflow: read the Selenium test + page objects → summarize planned files → **ask for approval** → generate `playwright-ts/pages` + `playwright-ts/tests` → run `npm test` → fix issues. Selenium and Playwright suites stay independently runnable.

### 3. Playwright test-generation agents (bundled tooling)

The Playwright package under `playwright-ts/node_modules/playwright/lib/agents/` ships browser-driving agents used to author/repair specs by actually interacting with the live site (the second cart test case was authored this way):

- **`playwright-test-generator`** — drives a real browser and writes a Playwright test from the observed interactions.
- **`playwright-test-healer`** — re-runs and repairs failing/flaky specs.
- **`playwright-test-planner`** — plans test scenarios before generation.

## Playwright TypeScript Migration

This repo also includes a Playwright TypeScript module under `playwright-ts/` that mirrors the Selenium Java flows:

```
playwright-ts/
├── pages/
│   ├── HomePage.ts
│   ├── ProductsPage.ts
│   └── CartPage.ts
├── tests/products.spec.ts   # products-page visibility + add-to-cart-and-verify-in-cart
├── utils/adOverlay.ts       # blocks ad-network requests; click-and-retry fallback for stray overlays
└── playwright.config.ts
```

`automationexercise.com` serves ad interstitials that can intercept clicks; `utils/adOverlay.ts` blocks the known ad domains (`doubleclick.net`, `googlesyndication.com`, etc.) at the network level via `page.route()`, with a reactive dismiss-and-retry click as a fallback.

Run Playwright tests:

```bash
cd playwright-ts
npm test
```

Run headed:

```bash
cd playwright-ts
npm run test:headed
```

## Roadmap / Yet to Implement

The framework is intentionally scaffolded for UI + API + Mobile, but only the **web UI** path is complete today. Notes on what's still open:

- **API testing** — ~~`rest-assured` is already a dependency, but no API tests, request/response models, or service layer exist yet.~~ **Done.** `ApiBase` provides shared request specs (JSON + form-encoded), and `ProductsApiTest` covers products list, brands list, and product search endpoints with both happy-path and negative tests. Run with `mvn test -DsuiteXmlFile=src/test/resources/testNG-api.xml`.
- **Mobile (Appium)** — ~~`WebDriverFactory` has an `AndroidDriver` path but has not been run against a real emulator/device.~~ **Done.** Mobile web testing works end-to-end via Appium + Android Emulator. The same `ProductsTest` flows run on mobile Chrome. See [Mobile Web Testing Setup](#mobile-web-testing-setup). Native app testing (APK) is not yet implemented.
- **Double execution** — the same scenarios currently run twice on `mvn test` (once via the plain-TestNG `ProductsTest`, once via `CucumberRunner`). Acceptable for now; can be scoped later with a `testng.xml` suite or Maven profiles.
- **CI pipeline** — no CI workflow is wired up yet (build + Selenium + Playwright suites on push/PR).
- **Cross-browser / parallel runs** — the `ThreadLocal` driver design supports parallelism, but no parallel `testng.xml` config or cross-browser matrix is defined yet.
- **More coverage** — only two web flows (products navigation, add-to-cart) exist so far; broader scenarios (checkout, login/signup, negative cases) are not yet written.
