# Robust Hybrid Automation Framework

A hybrid test automation framework for driving **UI (Selenium)**, **API (rest-assured)**, and **Mobile (Appium)** tests through a single **Cucumber-BDD + TestNG** harness, with **Allure** reporting.

> **Status:** early skeleton (`feature/framework-skeleton`). The package structure, configuration loading, and driver scaffolding are in place; page objects, step definitions, runners, and the driver-creation logic are still to be implemented.

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
```

> Run Maven from the **project root** — configuration is loaded via the relative path `src/main/resources/config.properties`, so a different working directory will break it.

## Configuration

All tunable values live in `src/main/resources/config.properties` and are read through `ConfigReader` (never hardcoded elsewhere):

| Key | Purpose |
|---|---|
| `platform` | `web` or `mobile` — selects the Selenium vs. Appium driver path |
| `browser` | Target browser for web runs |
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
├── driver/WebDriverFactory.java      # ThreadLocal<WebDriver> lifecycle (parallel-safe)
└── constants/FrameworkConstants.java # Shared paths (config, screenshots, allure-results)

src/main/resources/config.properties  # Central configuration
src/test/java/com/hemanth/automation/  # TestNG tests (e.g. tests/ConfigReaderTest)
```

## Reporting

Allure results are written to `target/allure-results/` (generated output; not committed). View them with the Allure CLI:

```bash
allure serve target/allure-results
```

> The `allure-maven` plugin is not yet configured, so there is no `mvn allure:report` goal.

## AI Assistant Instructions

Contributor and AI-assistant guidance lives in [`AGENTS.md`](./AGENTS.md) (the single source of truth). [`CLAUDE.md`](./CLAUDE.md) and [`.github/copilot-instructions.md`](./.github/copilot-instructions.md) point to it.
