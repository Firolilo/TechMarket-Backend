# Repository Guidelines

## Project Structure & Module Organization

This repository contains four Java 21 Maven services. `TechMarket-Core/` holds shared platform code in `core-platform/`. `TechMarket-IA/` and `TechMarket-IAM/` are Spring Boot services with source under `src/main/java`, tests under `src/test/java`, and Flyway migrations in `src/main/resources/db/migration`. `TechMarket-AI/` is a multi-module service: `modules/ai-domain`, `ai-application`, `ai-infrastructure`, `ai-api`, and `ai-bootstrap`. Operational docs live in local `README.md` files; root notes such as `endpoint.md` are reference material, not build inputs.

## Build, Test, and Development Commands

Run commands from the module you are changing:

- `./mvnw test` or `mvn test`: run the module test suite.
- `./mvnw clean package`: compile, test, and package a service jar.
- `./mvnw spring-boot:run`: start `TechMarket-IA` or `TechMarket-IAM` locally.
- `docker compose up --build`: start local dependencies and the selected service in modules that ship a `docker-compose.yml`.
- `./mvnw spotless:apply`: apply Java formatting before committing when `spotless:check` fails.

For `TechMarket-AI`, use the wrapper from `TechMarket-AI/`; for shared code, build `TechMarket-Core/` first if a service depends on `core-platform`.

## Coding Style & Naming Conventions

Use 4-space indentation, UTF-8, LF endings, and final newlines; `TechMarket-IA/.editorconfig` sets the baseline followed across the repo. Java formatting is enforced with Spotless and Google Java Format AOSP style. Keep package names lowercase, classes in `PascalCase`, methods and fields in `camelCase`, and test classes suffixed with `Test` or `ArchitectureTest`. In `TechMarket-AI`, preserve the clean architecture boundaries: domain and application modules must not depend on Spring.

## Testing Guidelines

JUnit-based tests live beside each module. Common patterns include controller tests such as `CompleteControllerTest`, service tests such as `RagQaServiceTest`, and architecture checks such as `CoreArchitectureTest` and `IamArchitectureTest`. Keep new test names aligned with the class under test. Run `./mvnw test` before opening a PR; use the `pact` profile in `TechMarket-IAM` only when working on provider contract tests.

## Commit & Pull Request Guidelines

Recent history uses short, lowercase commit subjects, sometimes with a scope prefix such as `docs:` or `chore:`. Follow that pattern and keep each commit focused on one module or concern. PRs should state the affected service, summarize behavior changes, list the verification command used, and mention migration, security, or contract-test impact when relevant.
