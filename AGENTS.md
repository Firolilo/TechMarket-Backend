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

Endpoints 56-125 are implemented in `TechMarket-IA` for the Ambassador module under `/api/ambassadors/**`. Ambassador endpoints use the `X-User-Id` header as authenticated ambassador context until IAM security integration is wired locally. Ambassador controllers implemented for this contract must stay under packages containing `api.admin`; the older legacy `ambassadors.api.AmbassadorController` still exists separately.

Endpoints 126-174 are implemented in `TechMarket-IA` for specialist profile, services, portfolio, availability, calendar, requests, projects, history, chats, files, wallet, transactions, withdrawals, earnings, reviews, certifications, and deterministic AI assistant endpoints under `/api/specialists/**`. Like the client endpoints, they use the `X-User-Id` header as the authenticated specialist context until IAM security integration is wired locally. Specialist controllers must also stay under packages containing `api.admin` to satisfy the IA architecture test.

Endpoints 175-180 are implemented in `TechMarket-IA` for general search under `/api/search/**`. Global search reads listings, tenants, specialist services, and communities through a JDBC search repository. Search history uses the `X-User-Id` header as authenticated user context.

Endpoints 181-200 are implemented in `TechMarket-IA` for general notifications, unified conversations, payment methods, payment intents, and user transactions under `/api/notifications/**`, `/api/conversations/**`, `/api/messages/**`, `/api/payments/**`, and `/api/transactions/**`. These endpoints use the `X-User-Id` header as authenticated user context until IAM security integration is wired locally. Controllers must stay under packages containing `api.admin`.

Endpoints 201-220 are implemented in `TechMarket-IA` for user invoices, payment refunds, support tickets, reports, public configuration, and initial admin dashboard/users under `/api/invoices/**`, `/api/payments/refunds`, `/api/support/tickets/**`, `/api/reports/**`, `/api/config/**`, and `/api/admin/**`. Authenticated user endpoints use `X-User-Id`; admin endpoints currently expose repository-backed aggregates without IAM admin authorization because cross-service security is not wired locally.

Endpoints 221-226 are implemented in `TechMarket-IA` for admin user status changes, report review workflow, moderation queue/actions, and audit logs under `/api/admin/**`. Admin action endpoints accept optional `X-Admin-User-Id` for audit attribution; IAM admin authorization is still not wired locally.

Implemented IA endpoint groups:

- 9-15: client profile and addresses under `/api/clients/profile` and `/api/clients/addresses`.
- 16-22: marketplace products, categories, and companies under `/api/marketplace`.
- 23-31: client cart, checkout, order history/detail, and order cancellation.
- 32-36: product/company reviews and public product review listing.
- 37-40: client chats and chat messages.
- 41-50: chat read marker, product/company favorites, and client communities join/leave.
- 51-55: community posts and client notifications read/read-all/delete.
- 56-61: ambassador profile, profile photo URL, stats, and settings.
- 62-69: ambassador referral links, QR URL, and active referral codes.
- 70-79: ambassador referrals, detail/update/status/delete, activity, notes, and files.
- 80-87: ambassador onboarding list/detail/tasks/reminders/milestones.
- 88-90: ambassador lead list/create/detail.
- 91-94: ambassador lead update, status change, conversion to referred business/user, and deletion.
- 95-100: ambassador commissions list/summary/detail, commission disputes, wallet summary, and withdrawal requests.
- 101-104: ambassador payout history and payout method list/create/delete.
- 105-110: ambassador direct network, tree, invitations create/list/delete, and ranking.
- 111-115: ambassador chats list/create/messages/send/read marker.
- 116-120: ambassador performance, referrals, commissions, funnel, and report export.
- 121-125: deterministic ambassador AI query, insights, prospect score, follow-up suggestion, and improvement plan.
- 126-140: specialist profile, stats, profile photo URL, services, portfolio, and availability/status.
- 141-150: specialist calendar, manual calendar blocks, service requests, project list/detail/status/history, and active chats.
- 151-160: specialist chat messages/files, personal files, wallet summary, and transaction list/detail.
- 161-170: specialist withdrawal requests, earnings summary, received reviews/replies, certifications/verification, and deterministic AI query response.
- 171-174: specialist AI insights, pricing suggestion, improvement plan, and schedule optimization.
- 175-180: global search, suggestions, trending searches, and authenticated search history create/list/delete.
- 181-187: general notifications, unread count, read/read-all/delete, and notification preferences.
- 188-193: unified conversations, messages, read marker, and user-owned message deletion.
- 194-200: saved payment methods, payment intents, payment confirmation, and user transactions.
- 201-203: user invoices, invoice download URL, and payment refund requests.
- 204-208: support tickets, support ticket messages, and support ticket status updates.
- 209-211: user-created moderation reports.
- 212-218: public configuration for countries, cities, currencies, categories, user types, platform, and payment options.
- 219-220: admin dashboard aggregate and registered user listing.
- 221-226: admin user status updates, report status review, moderation queue/actions, and audit log listing.

IA persistence additions for those ranges include Flyway migrations `V72__client_addresses.sql`, `V73__client_cart_and_orders.sql`, `V74__client_reviews_and_chat_support.sql`, `V75__client_favorites_and_communities.sql`, and `V76__client_community_posts_and_notification_links.sql`. Reviews reuse the existing `reviews` table with `listing_id` and `updated_at` added by `V74`; chats reuse `tickets` and `ticket_messages` with `ticket_type = 'CHAT'`; product/company favorites reuse `favorites`; communities use `communities` and `community_memberships`; community posts reuse `feed_posts`; notifications reuse `notifications`.

Ambassador persistence for endpoints 56-70 is added by `V84__ambassador_profile_referral_links.sql` plus existing ambassador tables. Ambassador profile/settings extend `ambassadors`; referral links use `ambassador_referral_links`; stats read `ambassador_referrals` and `ambassador_commissions`; referral codes are derived from active referral links or `ambassadors.referral_code`.

Ambassador persistence for endpoints 71-90 is added by `V85__ambassador_referrals_onboarding_leads.sql` plus existing ambassador tables. Referral details extend `ambassador_referrals`; referral activity, notes, and files use `ambassador_referral_activity`, `ambassador_referral_notes`, and `ambassador_referral_files`; onboarding tasks, reminders, and milestone state use `ambassador_onboarding_tasks`, `ambassador_onboarding_reminders`, and `ambassador_onboarding_milestones`; captured leads use `ambassador_leads`.

Ambassador persistence for endpoints 91-100 is added by `V86__ambassador_commission_disputes_withdrawals.sql` plus existing ambassador tables. Lead update/status/convert/delete reuse `ambassador_leads` and `ambassador_referrals`; commissions read `ambassador_commissions`; commission disputes use `ambassador_commission_disputes`; wallet withdrawals use `ambassador_withdrawals`.

Ambassador persistence for endpoints 101-115 is added by `V87__ambassador_payout_methods_network_chats.sql` plus existing chat tables. Payout history reuses `ambassador_withdrawals`; payout methods use `ambassador_payout_methods`; ambassador network uses `ambassadors.sponsor_ambassador_id`; invitations use `ambassador_invitations`; chats reuse `tickets`, `ticket_messages`, and `chat_read_receipts` with `ticket_type = 'AMBASSADOR_CHAT'`.

Ambassador endpoints 116-125 reuse the existing ambassador data model. Reports aggregate `ambassador_referral_links`, `ambassador_leads`, `ambassador_referrals`, and `ambassador_commissions`; report export returns a deterministic download URL; ambassador AI endpoints are deterministic responses computed from lead/referral context and do not require new persistence.

Specialist persistence for endpoints 126-140 is added by `V77__specialist_profile_services_portfolio_availability.sql`. Specialist profiles, services, portfolio items, and availability use dedicated `specialist_*` tables; stats read completed work from `service_appointments.assigned_technician_user_id` and review totals/averages from `reviews.ticket_id` linked to those appointments.

Specialist persistence for endpoints 141-150 is added by `V78__specialist_calendar_blocks.sql` plus existing operational tables. Calendar blocks use `specialist_calendar_blocks`; calendar, requests, projects, and history read/update `service_appointments` joined with `tickets` and `users`; specialist chats reuse `tickets`, `ticket_messages`, and `chat_read_receipts` with `assigned_technician_user_id`.

Specialist persistence for endpoints 151-160 is added by `V79__specialist_files_and_transactions.sql` plus existing chat tables. Specialist chat messages reuse `ticket_messages`; chat files reuse `ticket_attachments` with an added `file_size` column; personal files use `specialist_files`; wallet and transaction endpoints use `specialist_transactions`.

Specialist persistence for endpoints 161-170 is added by `V80__specialist_withdrawals_certifications_ai_reviews.sql`. Withdrawals use `specialist_withdrawals`; certifications use `specialist_certifications`; AI query history uses `specialist_ai_queries`; review replies add `technician_response` and `technician_response_at` to `reviews` while review lists/details still join `reviews`, `service_appointments`, `tickets`, and `users`.

Search persistence for endpoints 175-180 is added by `V81__global_search_history.sql`. Search history uses `search_history`; trending terms use `search_trends`; global search itself reads existing platform tables and does not duplicate indexed content.

General notification, conversation, and payment persistence for endpoints 181-200 is added by `V82__general_notifications_conversations_payments.sql` plus existing notification/chat tables. Notifications reuse `notifications`; preferences use `user_notification_preferences`; conversations reuse `tickets`, `ticket_messages`, and `chat_read_receipts`; payment methods use `user_payment_methods`; intents use `user_payment_intents`; user transaction history uses `user_transactions`.

Billing, support, reports, config, and admin persistence for endpoints 201-220 is added by `V83__general_billing_support_reports.sql` plus existing operational tables. Invoices use `user_invoices`; refunds use `user_payment_refunds`; support tickets reuse `tickets` with `ticket_type = 'SUPPORT'` plus `ticket_messages`; reports use `user_reports`; config categories reuse `catalog_categories`; admin users read `users` and dashboard counts read `users`, `tenants`, `user_reports`, and `user_transactions`.

Admin moderation persistence for endpoints 221-226 reuses `users`, `user_reports`, `moderation_actions`, and `audit_logs`. `user_reports.admin_action` is added by `V83__general_billing_support_reports.sql` for report review decisions. Moderation actions store object IDs in `moderation_actions.entity_id`; non-UUID object identifiers are mapped deterministically to UUIDs for local persistence.

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
- Conversations: `CONV-{uuid}`
- Communities: `COM-{uuid}`
- Community posts: `POST-{uuid}`
- Notifications: `NOT-{uuid}`
- Ambassadors: `AMB-{uuid}`
- Ambassador referral links: `REFLINK-{uuid}`
- Ambassador referred businesses/users: `BUS-{uuid}`
- Ambassador referral activity: `ACT-{uuid}`
- Ambassador referral notes: `NOTE-{uuid}`
- Ambassador onboarding: `ONB-{uuid}`
- Ambassador onboarding tasks: `TASK-{uuid}`
- Ambassador onboarding reminders: `REM-{uuid}`
- Ambassador onboarding milestones: `MLS-{number}`
- Ambassador leads: `LEAD-{uuid}`
- Ambassador commissions: `COM-{uuid}`
- Ambassador commission disputes: `DSP-{uuid}`
- Ambassador withdrawals: `WDR-{uuid}`
- Ambassador payout methods: `PAYM-{uuid}`
- Ambassador invitations: `INV-AMB-{uuid}`
- Ambassador chats: `CHT-AMB-{uuid}`
- Specialists: `TEC-{uuid}`
- Specialist services: `SERV-{uuid}`
- Specialist portfolio items: `PORT-{uuid}`
- Specialist calendar blocks: `BLK-{uuid}`
- Specialist requests: `REQ-{uuid}`
- Specialist projects: `PROJ-{uuid}`
- Specialist files/chat attachments: `FILE-{uuid}`
- Specialist transactions: `TX-{uuid}`
- Specialist certifications: `CERT-{uuid}`
- Search history: `SRH-{uuid}`
- Payment methods: `PM-{uuid}`
- Payment intents: `PAY-{uuid}`
- General transactions: `TRX-{uuid}`
- Invoices: `INV-{uuid}`
- Refunds: `RFD-{uuid}`
- Support tickets: `TCK-{uuid}`
- Support ticket messages: `TMSG-{uuid}`
- Reports: `REP-{uuid}`
- Moderation queue items: `MOD-{uuid}`
- Moderation actions: `ACT-{uuid}`
- Audit logs: `AUD-{uuid}`
- Admin actors: `ADM-{uuid}`

Known verification status: `mvn test -DskipTests -Dspotless.check.skip=true` passes in both `TechMarket-IA` and `TechMarket-IAM` after the endpoint work. Full `mvn test` currently fails in this environment because Mockito/ByteBuddy cannot self-attach under Java 25, and `spotless:check` fails because the configured `google-java-format` is incompatible with the current JDK. Prefer Java 21 for full local verification.

## Commit & Pull Request Guidelines

Recent history uses short, lowercase commit subjects, sometimes with a scope prefix such as `docs:` or `chore:`. Follow that pattern and keep each commit focused on one module or concern. PRs should state the affected service, summarize behavior changes, list the verification command used, and mention migration, security, or contract-test impact when relevant.
