# AGENTS.md

Shared, tool-agnostic instructions for any AI coding assistant working in this repository (Claude Code, GitHub Copilot, Codex, Cursor, etc.). This is the single source of truth — other tools' instruction files point here.

## Operating Mindset

Approach every task in this repository as a **senior automation architect**. That means:

- Think about framework design, not just making a single test pass — favor reusability, maintainability, and clean separation of concerns (config, drivers, page objects, tests).
- Keep the design thread-safe and parallel-ready (e.g. the `ThreadLocal` driver pattern); don't introduce static/shared mutable state that breaks parallel runs.
- Prefer config-driven behavior over hardcoded values, and consistent patterns over one-off solutions.
- Call out design trade-offs, scalability concerns, and flakiness risks proactively rather than silently picking an approach.

## Overview

A **hybrid test automation framework** intended to drive UI (Selenium), API (rest-assured), and Mobile (Appium) tests through a single Cucumber-BDD + TestNG harness, with Allure reporting. The repository is currently an early **skeleton** (`feature/framework-skeleton`): the package structure, config loading, and driver scaffolding exist, but most automation logic (page objects, step definitions, the driver-creation body, runners) is not yet implemented.

## Build & Test Commands

Maven project, Java 17.

```bash
mvn clean install          # build + run tests
mvn test                   # run all tests (TestNG via Surefire)
mvn test -Dtest=ConfigReaderTest                       # single test class
mvn test -Dtest=ConfigReaderTest#verifyConfigReader    # single test method
```

- Run Maven from the **project root** — `ConfigReader` loads `config.properties` via the relative path `src/main/resources/config.properties`, so a different working directory breaks it.
- Allure results are written under `target/allure-results/` (see `FrameworkConstants`). View with the Allure CLI: `allure serve target/allure-results`. Note: the `allure-maven` plugin is **not** configured in `pom.xml`, so there is no `mvn allure:report` goal yet.

## Test Framework Convention

**TestNG only — do not use JUnit.** JUnit was deliberately removed from this project (there is no JUnit dependency in `pom.xml`). All tests use `org.testng.annotations.*`. The Cucumber integration is `cucumber-testng`, not `cucumber-junit`.

## Architecture

Single Maven module under `com.hemanth.automation`:

- **`config.ConfigReader`** — central, app-wide configuration access. Loads `src/main/resources/config.properties` once in a static initializer and exposes `getProperty(key)` / `getProperty(key, default)`. All tunable values (browser, headless, base URL, waits, platform, Appium settings) flow through here; **do not hardcode** these values elsewhere.
- **`driver.WebDriverFactory`** — owns the `WebDriver` lifecycle via a `ThreadLocal<WebDriver>`, so the design is thread-safe for parallel execution. Currently a stub (private constructor + the ThreadLocal field); the create/get/quit methods that read config and instantiate the local or Appium driver still need to be written here.
- **`constants.FrameworkConstants`** — non-instantiable holder for shared paths (`CONFIG_FILE_PATH`, `SCREENSHOT_PATH`, `ALLURE_RESULTS_PATH`).
- **`tests`** — TestNG test classes (e.g. `ConfigReaderTest`).

### Configuration (`src/main/resources/config.properties`)

The `platform` key (`web` / `mobile`) is the intended switch that selects between the Selenium and Appium driver paths in `WebDriverFactory`. Other keys: `browser`, `headless`, `base.url`, `implicit.wait`, `explicit.wait`, and the `appium.server.url` / `mobile.platform` / `android.device.name` / `android.app.path` group for mobile runs.

## Notes for extending the framework

- New driver logic belongs in `WebDriverFactory` and must keep the `ThreadLocal` pattern (set on creation, `remove()` on quit) to stay parallel-safe.
- New file-system or report paths belong in `FrameworkConstants`; new tunable settings belong in `config.properties` and are read through `ConfigReader`.
- `App.java` is leftover Maven-archetype boilerplate (`Hello World` main) and is not part of the framework.
