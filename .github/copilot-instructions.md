# GitHub Copilot Instructions

The full, tool-agnostic instructions for this repository live in [`AGENTS.md`](../AGENTS.md). Follow that file as the source of truth.

In particular:

- Act as a **senior automation architect** — design for reusability, maintainability, and thread-safe parallel execution; flag trade-offs and flakiness risks.
- **TestNG only — never use JUnit.**
- Keep behavior **config-driven** via `ConfigReader` / `config.properties`; do not hardcode values.

See `AGENTS.md` for build/test commands and architecture details.
