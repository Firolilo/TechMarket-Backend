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

Endpoints 56-125 belong to the Ambassador module (`/api/ambassadors/**`) and are intentionally ignored for now. Do not implement that range unless the user explicitly asks to resume Ambassador work.

Endpoints 126-170 are implemented in `TechMarket-IA` for specialist profile, services, portfolio, availability, calendar, requests, projects, history, chats, files, wallet, transactions, withdrawals, earnings, reviews, certifications, and AI query under `/api/specialists/**`. Like the client endpoints, they use the `X-User-Id` header as the authenticated specialist context until IAM security integration is wired locally. Specialist controllers must also stay under packages containing `api.admin` to satisfy the IA architecture test.

Implemented IA endpoint groups:

- 9-15: client profile and addresses under `/api/clients/profile` and `/api/clients/addresses`.
- 16-22: marketplace products, categories, and companies under `/api/marketplace`.
- 23-31: client cart, checkout, order history/detail, and order cancellation.
- 32-36: product/company reviews and public product review listing.
- 37-40: client chats and chat messages.
- 41-50: chat read marker, product/company favorites, and client communities join/leave.
- 51-55: community posts and client notifications read/read-all/delete.
- 126-140: specialist profile, stats, profile photo URL, services, portfolio, and availability/status.
- 141-150: specialist calendar, manual calendar blocks, service requests, project list/detail/status/history, and active chats.
- 151-160: specialist chat messages/files, personal files, wallet summary, and transaction list/detail.
- 161-170: specialist withdrawal requests, earnings summary, received reviews/replies, certifications/verification, and deterministic AI query response.

IA persistence additions for those ranges include Flyway migrations `V72__client_addresses.sql`, `V73__client_cart_and_orders.sql`, `V74__client_reviews_and_chat_support.sql`, `V75__client_favorites_and_communities.sql`, and `V76__client_community_posts_and_notification_links.sql`. Reviews reuse the existing `reviews` table with `listing_id` and `updated_at` added by `V74`; chats reuse `tickets` and `ticket_messages` with `ticket_type = 'CHAT'`; product/company favorites reuse `favorites`; communities use `communities` and `community_memberships`; community posts reuse `feed_posts`; notifications reuse `notifications`.

Specialist persistence for endpoints 126-140 is added by `V77__specialist_profile_services_portfolio_availability.sql`. Specialist profiles, services, portfolio items, and availability use dedicated `specialist_*` tables; stats read completed work from `service_appointments.assigned_technician_user_id` and review totals/averages from `reviews.ticket_id` linked to those appointments.

Specialist persistence for endpoints 141-150 is added by `V78__specialist_calendar_blocks.sql` plus existing operational tables. Calendar blocks use `specialist_calendar_blocks`; calendar, requests, projects, and history read/update `service_appointments` joined with `tickets` and `users`; specialist chats reuse `tickets`, `ticket_messages`, and `chat_read_receipts` with `assigned_technician_user_id`.

Specialist persistence for endpoints 151-160 is added by `V79__specialist_files_and_transactions.sql` plus existing chat tables. Specialist chat messages reuse `ticket_messages`; chat files reuse `ticket_attachments` with an added `file_size` column; personal files use `specialist_files`; wallet and transaction endpoints use `specialist_transactions`.

Specialist persistence for endpoints 161-170 is added by `V80__specialist_withdrawals_certifications_ai_reviews.sql`. Withdrawals use `specialist_withdrawals`; certifications use `specialist_certifications`; AI query history uses `specialist_ai_queries`; review replies add `technician_response` and `technician_response_at` to `reviews` while review lists/details still join `reviews`, `service_appointments`, `tickets`, and `users`.

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
- Specialists: `TEC-{uuid}`
- Specialist services: `SERV-{uuid}`
- Specialist portfolio items: `PORT-{uuid}`
- Specialist calendar blocks: `BLK-{uuid}`
- Specialist requests: `REQ-{uuid}`
- Specialist projects: `PROJ-{uuid}`
- Specialist files/chat attachments: `FILE-{uuid}`
- Specialist transactions: `TX-{uuid}`
- Specialist certifications: `CERT-{uuid}`

Known verification status: `mvn test -DskipTests -Dspotless.check.skip=true` passes in both `TechMarket-IA` and `TechMarket-IAM` after the endpoint work. Full `mvn test` currently fails in this environment because Mockito/ByteBuddy cannot self-attach under Java 25, and `spotless:check` fails because the configured `google-java-format` is incompatible with the current JDK. Prefer Java 21 for full local verification.

## Commit & Pull Request Guidelines

Recent history uses short, lowercase commit subjects, sometimes with a scope prefix such as `docs:` or `chore:`. Follow that pattern and keep each commit focused on one module or concern. PRs should state the affected service, summarize behavior changes, list the verification command used, and mention migration, security, or contract-test impact when relevant.
