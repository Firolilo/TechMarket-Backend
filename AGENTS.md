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

## Endpoint Implementation Context

`endpoint.md` is the working reference for the platform API. It is now formatted as Markdown and all endpoint definitions are globally numbered from 1 to 226 using `#### N. METHOD /path`. Keep this numbering stable when adding or implementing new endpoints so future work can continue by ranges.

Endpoints 1-8 are implemented in `TechMarket-IAM` for authentication and user profiles: registration, login, logout, refresh token, forgot password, authenticated profile read/update, and public user profile. The IAM vertical was also reviewed for duplicate routes, OTP login flow support, profile entity wiring, public registration profile migration, and security permits for auth/profile/public user routes.

Endpoints 9-55 are implemented in `TechMarket-IA`. Client-facing controllers must stay under packages containing `api.admin` because the IA architecture test expects controller classes there, even for marketplace or client routes. The current client endpoint implementation uses the `X-User-Id` header as the authenticated user context because IA does not yet have the IAM security integration wired locally.

Implemented IA endpoint groups:

- 9-15: client profile and addresses under `/api/clients/profile` and `/api/clients/addresses`.
- 16-22: marketplace products, categories, and companies under `/api/marketplace`.
- 23-31: client cart, checkout, order history/detail, and order cancellation.
- 32-36: product/company reviews and public product review listing.
- 37-40: client chats and chat messages.
- 41-50: chat read marker, product/company favorites, and client communities join/leave.
- 51-55: community posts and client notifications read/read-all/delete.

IA persistence additions for those ranges include Flyway migrations `V72__client_addresses.sql`, `V73__client_cart_and_orders.sql`, `V74__client_reviews_and_chat_support.sql`, `V75__client_favorites_and_communities.sql`, and `V76__client_community_posts_and_notification_links.sql`. Reviews reuse the existing `reviews` table with `listing_id` and `updated_at` added by `V74`; chats reuse `tickets` and `ticket_messages` with `ticket_type = 'CHAT'`; product/company favorites reuse `favorites`; communities use `communities` and `community_memberships`; community posts reuse `feed_posts`; notifications reuse `notifications`.

ID formatting conventions used by the implemented endpoints:

- Users: `USR-{uuid}`
- Products/listings: `PROD-{uuid}`
- Categories: `CAT-{uuid}`
- Companies/tenants: `EMP-{uuid}`
- Addresses: `ADDR-{uuid}`
- Cart: `CART-{userUuid}`
- Cart items: `ITEM-{uuid}`
- Orders: `ORD-{uuid}`
- Reviews: `REV-{uuid}`
- Chats: `CHT-{uuid}`
- Chat messages: `MSG-{uuid}`
- Communities: `COM-{uuid}`
- Community posts: `POST-{uuid}`
- Notifications: `NOT-{uuid}`

Known verification status: `mvn test -DskipTests -Dspotless.check.skip=true` passes in both `TechMarket-IA` and `TechMarket-IAM` after the endpoint work. Full `mvn test` currently fails in this environment because Mockito/ByteBuddy cannot self-attach under Java 25, and `spotless:check` fails because the configured `google-java-format` is incompatible with the current JDK. Prefer Java 21 for full local verification.

## Commit & Pull Request Guidelines

Recent history uses short, lowercase commit subjects, sometimes with a scope prefix such as `docs:` or `chore:`. Follow that pattern and keep each commit focused on one module or concern. PRs should state the affected service, summarize behavior changes, list the verification command used, and mention migration, security, or contract-test impact when relevant.
