# Selenium Java Test Case Generator

Follow AGENTS.md as the source of truth.

Generate Selenium Java TestNG test cases for the requested user flow on automationexercise.com using the existing framework conventions.

## Before writing any code

1. Read the existing page objects under `src/main/java/com/hemanth/automation/pages/` to understand what actions and locators are already available.
2. Read existing test classes under `src/test/java/com/hemanth/automation/tests/` to match the established style.
3. Identify whether the requested flow can be covered with existing page objects or requires new ones.
4. Summarize the plan — which page objects to create/update, which test class to create/update, and what locators are needed.
5. Ask for approval before editing any files.

## After approval

### Page objects

- New page objects go in `src/main/java/com/hemanth/automation/pages/`.
- Every page object extends `BasePage`.
- Locators are `private final By` fields declared at the top of the class.
- Use `BasePage` helpers (`click`, `jsClick`, `isDisplayed`, `getText`, `dismissAdOverlayIfPresent`) — do not call Selenium/WebDriverWait directly.
- Public methods expose page actions and verifications. Keep locators out of test classes.
- Call `dismissAdOverlayIfPresent()` before interactions on pages that may show ad overlays (the target site serves ad interstitials).
- Use `jsClick` when a normal `click` is blocked by overlays or non-clickable elements; prefer `click` otherwise.
- Read the base URL from config (`ConfigReader.getProperty("base.url")`), never hardcode it.

### Test classes

- New UI test classes go in `src/test/java/com/hemanth/automation/tests/`.
- Every UI test class extends `BaseTest` — this handles `@BeforeMethod`/`@AfterMethod` driver lifecycle and registers `ScreenshotListener`.
- Use **TestNG only** — never JUnit.
- Annotate with Allure annotations: `@Epic("E-Commerce")` on the class, `@Feature("...")` for the functional area, `@Story("...")` on each test method.
- Each `@Test` method should be independent — no test should depend on another test's state.
- Assert with `Assert.assertTrue`, `Assert.assertEquals`, etc. from `org.testng.Assert`. Include a descriptive failure message.
- Instantiate page objects inside the test method, not as class fields (matches existing pattern).

### API test classes

- API tests go in `src/test/java/com/hemanth/automation/tests/api/`.
- API tests do **not** extend `BaseTest` (no browser needed).
- Use `ApiBase.requestSpec()` for JSON endpoints and `ApiBase.formRequestSpec()` for form-param endpoints.
- Add new API test classes to `src/test/resources/testNG-api.xml`.

### BDD scenarios (if requested)

- Feature files go in `src/test/resources/features/`.
- Step definitions go in `src/test/java/com/hemanth/automation/stepdefinitions/`.
- Reuse the same page objects; do not duplicate page logic in step definitions.
- If adding a new glue package, add it to `CucumberRunner`'s `@CucumberOptions(glue = ...)`.

## Naming conventions

- Test class: `<Feature>Test.java` (e.g., `LoginTest.java`, `CheckoutTest.java`).
- Page object: `<PageName>Page.java` (e.g., `LoginPage.java`, `SignupPage.java`).
- Test method: `userCan<Action>` or descriptive camelCase (e.g., `userCanLoginWithValidCredentials`).
- Feature file: `<feature>.feature` (e.g., `login.feature`).

## After generating

1. Verify the code compiles:
   ```bash
   mvn compile -q test-compile -q
   ```
2. Run the new test:
   ```bash
   mvn test -Dtest=<TestClassName>
   ```
3. Fix any compilation, locator, or runtime issues.
4. Do not modify existing tests unless the change is required for the new flow to work.
