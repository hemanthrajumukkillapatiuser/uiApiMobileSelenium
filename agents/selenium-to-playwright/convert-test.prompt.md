# Selenium to Playwright Conversion Prompt

Follow AGENTS.md as the source of truth.

Convert the requested Selenium Java test into Playwright TypeScript.

Before changing files:

1. Read the Selenium test class.
2. Read all related Java Page Object classes.
3. Summarize the planned files to create/update.
4. Ask for approval before editing.

After approval:

1. Create or update Playwright page objects under `playwright-ts/pages`.
2. Create or update Playwright specs under `playwright-ts/tests`.
3. Use `@playwright/test`, async/await, Locator, and expect.
4. Preserve the original test intent and validations.
5. Run:

```bash
cd playwright-ts
npm test
```

6. Fix TypeScript, locator, or runtime issues.
7. Keep Selenium and Playwright tests runnable independently.
