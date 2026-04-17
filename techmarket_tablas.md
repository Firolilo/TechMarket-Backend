# Tablas de TechMarket

---

## 1. Identidad y acceso

### `users`
- `id`
- `first_name`
- `last_name`
- `email`
- `phone`
- `password_hash`
- `status`
- `last_login_at`
- `created_at`
- `updated_at`

### `roles`
- `id`
- `name`
- `description`

### `user_roles`
- `id`
- `user_id`
- `role_id`
- `is_active`
- `assigned_at`

### `permissions`
- `id`
- `code`
- `name`
- `description`

### `role_permissions`
- `id`
- `role_id`
- `permission_id`

---

## 2. Estructura multi-tenant del negocio

### `tenants`
- `id`
- `business_name`
- `legal_name`
- `tax_id`
- `business_type`
- `description`
- `status`
- `registered_at`
- `created_at`
- `updated_at`

### `tenant_members`
- `id`
- `tenant_id`
- `user_id`
- `tenant_role`
- `status`
- `invited_by_user_id`
- `joined_at`

### `branches`
- `id`
- `tenant_id`
- `name`
- `address`
- `city`
- `latitude`
- `longitude`
- `phone`
- `opening_hours`
- `is_main_branch`
- `status`
- `created_at`

### `tenant_verifications`
- `id`
- `tenant_id`
- `verification_status`
- `verification_type`
- `reviewed_by_user_id`
- `review_notes`
- `reviewed_at`
- `created_at`

### `tenant_verification_documents`
- `id`
- `tenant_verification_id`
- `document_type`
- `file_url`
- `status`
- `uploaded_by_user_id`
- `uploaded_at`

### `tenant_settings`
- `id`
- `tenant_id`
- `currency`
- `language`
- `time_zone`
- `whatsapp_number`
- `accepts_quotes`
- `accepts_tickets`
- `settings_json`

### `tenant_profiles`
- `id`
- `tenant_id`
- `logo_url`
- `cover_image_url`
- `short_description`
- `full_description`
- `website_url`
- `facebook_url`
- `instagram_url`
- `rating_average`
- `reviews_count`

### `business_categories`
- `id`
- `name`
- `description`

---

## 3. Catálogo y publicaciones

### `catalog_categories`
- `id`
- `name`
- `parent_category_id`
- `item_type`
- `slug`

### `brands`
- `id`
- `name`
- `slug`
- `logo_url`

### `listings`
- `id`
- `tenant_id`
- `category_id`
- `brand_id`
- `listing_type`
- `title`
- `description`
- `base_price`
- `currency`
- `status`
- `is_visible`
- `internal_sku`
- `created_at`
- `updated_at`

### `branch_listings`
- `id`
- `branch_id`
- `listing_id`
- `status`
- `is_available`
- `created_at`

### `listing_images`
- `id`
- `listing_id`
- `image_url`
- `display_order`
- `is_primary`

### `listing_specifications`
- `id`
- `listing_id`
- `attribute_name`
- `attribute_value`
- `unit`
- `is_normalized`

### `branch_inventory`
- `id`
- `listing_id`
- `branch_id`
- `stock_available`
- `stock_reserved`
- `minimum_stock`
- `updated_at`

### `service_details`
- `id`
- `listing_id`
- `estimated_duration_minutes`
- `requires_diagnosis`
- `offers_on_site_service`
- `service_area`
- `terms_and_conditions`

---

## 4. Descubrimiento e interacción del cliente

### `favorites`
- `id`
- `user_id`
- `tenant_id`
- `listing_id`
- `created_at`

### `search_history`
- `id`
- `user_id`
- `query_text`
- `search_type`
- `latitude`
- `longitude`
- `filters_json`
- `created_at`

### `interaction_events`
- `id`
- `user_id`
- `tenant_id`
- `listing_id`
- `event_type`
- `source_channel`
- `metadata_json`
- `created_at`

---

## 5. Leads y pipeline comercial

### `leads`
- `id`
- `tenant_id`
- `branch_id`
- `user_id`
- `listing_id`
- `source_channel`
- `status`
- `lead_score`
- `notes`
- `created_at`
- `updated_at`

### `lead_events`
- `id`
- `lead_id`
- `event_type`
- `performed_by_user_id`
- `details`
- `created_at`

### `lead_assignments`
- `id`
- `lead_id`
- `assigned_to_user_id`
- `assigned_at`
- `status`

---

## 6. Tickets y operación de servicios

### `tickets`
- `id`
- `ticket_code`
- `tenant_id`
- `branch_id`
- `customer_user_id`
- `lead_id`
- `listing_id`
- `assigned_user_id`
- `assigned_technician_user_id`
- `ticket_type`
- `subject`
- `description`
- `priority`
- `status`
- `opened_at`
- `closed_at`
- `created_at`

### `ticket_messages`
- `id`
- `ticket_id`
- `author_user_id`
- `message_body`
- `message_type`
- `is_visible_to_customer`
- `created_at`

### `ticket_attachments`
- `id`
- `ticket_id`
- `ticket_message_id`
- `file_url`
- `file_type`
- `original_file_name`
- `uploaded_by_user_id`
- `created_at`

### `ticket_status_history`
- `id`
- `ticket_id`
- `old_status`
- `new_status`
- `changed_by_user_id`
- `change_reason`
- `created_at`

### `service_appointments`
- `id`
- `ticket_id`
- `tenant_id`
- `assigned_technician_user_id`
- `start_at`
- `end_at`
- `location`
- `status`
- `notes`

---

## 7. Cotizaciones

### `quotes`
- `id`
- `ticket_id`
- `lead_id`
- `tenant_id`
- `customer_user_id`
- `status`
- `subtotal_amount`
- `discount_amount`
- `tax_amount`
- `total_amount`
- `currency`
- `valid_until`
- `created_at`
- `approved_at`

### `quote_items`
- `id`
- `quote_id`
- `listing_id`
- `description`
- `quantity`
- `unit_price`
- `line_total`

### `quote_status_history`
- `id`
- `quote_id`
- `old_status`
- `new_status`
- `changed_by_user_id`
- `created_at`

---

## 8. Inteligencia artificial y compatibilidad

### `technical_component_types`
- `id`
- `name`
- `technical_group`
- `description`

### `listing_components`
- `id`
- `listing_id`
- `component_type_id`

### `compatibility_rules`
- `id`
- `source_component_type_id`
- `source_attribute`
- `operator`
- `target_component_type_id`
- `target_attribute`
- `severity`
- `explanation_message`
- `is_active`

### `pc_builds`
- `id`
- `user_id`
- `name`
- `description`
- `status`
- `is_shareable`
- `created_at`

### `pc_build_items`
- `id`
- `pc_build_id`
- `listing_id`
- `component_type_id`
- `quantity`

### `intent_queries`
- `id`
- `user_id`
- `original_query`
- `detected_intent`
- `extracted_entities_json`
- `response_payload_json`
- `created_at`

---

## 9. Reseñas y capa social

### `reviews`
- `id`
- `tenant_id`
- `user_id`
- `ticket_id`
- `quote_id`
- `rating`
- `comment`
- `moderation_status`
- `created_at`

### `feed_posts`
- `id`
- `tenant_id`
- `author_user_id`
- `post_type`
- `title`
- `content`
- `status`
- `created_at`

### `feed_media`
- `id`
- `feed_post_id`
- `media_url`
- `media_type`
- `display_order`

### `post_comments`
- `id`
- `feed_post_id`
- `user_id`
- `comment_body`
- `status`
- `created_at`

---

## 10. Notificaciones y eventos del sistema

### `notifications`
- `id`
- `user_id`
- `notification_type`
- `title`
- `message`
- `channel`
- `is_read`
- `sent_at`

### `system_events`
- `id`
- `event_type`
- `entity_type`
- `entity_id`
- `tenant_id`
- `payload_json`
- `created_at`

### `notification_preferences`
- `id`
- `user_id`
- `notification_type`
- `email_enabled`
- `push_enabled`
- `whatsapp_enabled`
- `in_app_enabled`
- `created_at`
- `updated_at`

### `notification_channels`
- `id`
- `user_id`
- `channel_type`
- `destination`
- `is_verified`
- `is_active`
- `created_at`

---

## 11. Monetización, facturación y ledger

### `subscription_plans`
- `id`
- `name`
- `description`
- `price`
- `currency`
- `billing_period`
- `benefits_json`
- `is_active`

### `tenant_subscriptions`
- `id`
- `tenant_id`
- `subscription_plan_id`
- `status`
- `start_date`
- `end_date`
- `auto_renew`

### `invoices`
- `id`
- `tenant_id`
- `tenant_subscription_id`
- `invoice_number`
- `subtotal_amount`
- `tax_amount`
- `total_amount`
- `currency`
- `status`
- `issued_at`
- `due_at`

### `invoice_items`
- `id`
- `invoice_id`
- `description`
- `quantity`
- `unit_price`
- `line_total`

### `performance_charges`
- `id`
- `tenant_id`
- `reference_type`
- `reference_id`
- `charge_type`
- `amount`
- `status`
- `generated_at`

### `billing_disputes`
- `id`
- `tenant_id`
- `invoice_id`
- `performance_charge_id`
- `reason`
- `status`
- `resolved_by_user_id`
- `resolved_at`

### `ledger_entries`
- `id`
- `tenant_id`
- `ambassador_id`
- `entry_type`
- `reference_type`
- `reference_id`
- `debit_amount`
- `credit_amount`
- `currency`
- `description`
- `created_at`

### `payments`
- `id`
- `invoice_id`
- `tenant_id`
- `payment_method`
- `payment_provider`
- `external_reference`
- `amount`
- `currency`
- `status`
- `paid_at`

---

## 12. Embajadores y crecimiento distribuido

### `ambassadors`
- `id`
- `user_id`
- `referral_code`
- `status`
- `level`
- `activated_at`

### `ambassador_referrals`
- `id`
- `ambassador_id`
- `tenant_id`
- `attribution_channel`
- `used_code`
- `status`
- `created_at`

### `commission_rules`
- `id`
- `event_type`
- `level`
- `fixed_amount`
- `percentage_amount`
- `is_active`
- `valid_from`
- `valid_to`

### `ambassador_commissions`
- `id`
- `ambassador_id`
- `ambassador_referral_id`
- `commission_rule_id`
- `attribution_type`
- `event_type`
- `reference_type`
- `reference_id`
- `amount`
- `status`
- `generated_at`

### `payout_cycles`
- `id`
- `start_date`
- `end_date`
- `status`
- `total_amount`
- `generated_at`

### `payout_items`
- `id`
- `payout_cycle_id`
- `ambassador_id`
- `ambassador_commission_id`
- `amount`
- `status`

---

## 13. Fraude, moderación y control operativo

### `fraud_cases`
- `id`
- `case_type`
- `entity_type`
- `entity_id`
- `risk_level`
- `status`
- `reason`
- `created_at`
- `resolved_at`

### `moderation_actions`
- `id`
- `entity_type`
- `entity_id`
- `action_type`
- `reason`
- `performed_by_user_id`
- `created_at`

### `audit_logs`
- `id`
- `user_id`
- `tenant_id`
- `action_name`
- `entity_type`
- `entity_id`
- `ip_address`
- `user_agent`
- `created_at`

### `system_alerts`
- `id`
- `alert_type`
- `severity`
- `source`
- `description`
- `status`
- `created_at`
- `resolved_at`

---

## 14. Analítica y métricas

### `tenant_daily_metrics`
- `id`
- `tenant_id`
- `metric_date`
- `profile_views`
- `listing_views`
- `leads_count`
- `tickets_count`
- `quotes_count`
- `conversions_count`
- `revenue_amount`
- `average_response_time_minutes`

### `platform_daily_metrics`
- `id`
- `metric_date`
- `active_users_count`
- `active_tenants_count`
- `total_leads_count`
- `total_tickets_count`
- `conversion_rate`
- `total_revenue_amount`

---

## 15. Estado de migraciones (Flyway)

- Migraciones de tablas: `V2` a `V70` (1 archivo por tabla, formato `Vx__nombre_tabla.sql`)
- Migración de integridad y performance: `V71__add_foreign_keys_and_indexes.sql`

### Convención aplicada
- Cada tabla fue creada con `CREATE TABLE IF NOT EXISTS`
- Claves primarias en `id` con tipo `UUID`
- Columnas relacionales en `_id` con tipo `UUID`
- Fechas/hora en `_at` con tipo `TIMESTAMPTZ`
- Montos con tipo `NUMERIC(14,2)`

---

## 16. Relaciones FK implementadas (resumen)

### Identidad y acceso
- `user_roles.user_id` -> `users.id`
- `user_roles.role_id` -> `roles.id`
- `role_permissions.role_id` -> `roles.id`
- `role_permissions.permission_id` -> `permissions.id`

### Multi-tenant
- `tenant_members.tenant_id` -> `tenants.id`
- `tenant_members.user_id` -> `users.id`
- `tenant_members.invited_by_user_id` -> `users.id`
- `branches.tenant_id` -> `tenants.id`
- `tenant_verifications.tenant_id` -> `tenants.id`
- `tenant_verifications.reviewed_by_user_id` -> `users.id`
- `tenant_verification_documents.tenant_verification_id` -> `tenant_verifications.id`
- `tenant_verification_documents.uploaded_by_user_id` -> `users.id`
- `tenant_settings.tenant_id` -> `tenants.id`
- `tenant_profiles.tenant_id` -> `tenants.id`

### Catálogo y publicaciones
- `listings.tenant_id` -> `tenants.id`
- `listings.category_id` -> `catalog_categories.id`
- `listings.brand_id` -> `brands.id`
- `branch_listings.branch_id` -> `branches.id`
- `branch_listings.listing_id` -> `listings.id`
- `listing_images.listing_id` -> `listings.id`
- `listing_specifications.listing_id` -> `listings.id`
- `branch_inventory.listing_id` -> `listings.id`
- `branch_inventory.branch_id` -> `branches.id`
- `service_details.listing_id` -> `listings.id`

### Descubrimiento e interacción
- `favorites.user_id` -> `users.id`
- `favorites.tenant_id` -> `tenants.id`
- `favorites.listing_id` -> `listings.id`
- `search_history.user_id` -> `users.id`
- `interaction_events.user_id` -> `users.id`
- `interaction_events.tenant_id` -> `tenants.id`
- `interaction_events.listing_id` -> `listings.id`

### Leads y pipeline
- `leads.tenant_id` -> `tenants.id`
- `leads.branch_id` -> `branches.id`
- `leads.user_id` -> `users.id`
- `leads.listing_id` -> `listings.id`
- `lead_events.lead_id` -> `leads.id`
- `lead_events.performed_by_user_id` -> `users.id`
- `lead_assignments.lead_id` -> `leads.id`
- `lead_assignments.assigned_to_user_id` -> `users.id`

### Tickets y operación
- `tickets.tenant_id` -> `tenants.id`
- `tickets.branch_id` -> `branches.id`
- `tickets.customer_user_id` -> `users.id`
- `tickets.lead_id` -> `leads.id`
- `tickets.listing_id` -> `listings.id`
- `tickets.assigned_user_id` -> `users.id`
- `tickets.assigned_technician_user_id` -> `users.id`
- `ticket_messages.ticket_id` -> `tickets.id`
- `ticket_messages.author_user_id` -> `users.id`
- `ticket_attachments.ticket_id` -> `tickets.id`
- `ticket_attachments.ticket_message_id` -> `ticket_messages.id`
- `ticket_attachments.uploaded_by_user_id` -> `users.id`
- `ticket_status_history.ticket_id` -> `tickets.id`
- `ticket_status_history.changed_by_user_id` -> `users.id`
- `service_appointments.ticket_id` -> `tickets.id`
- `service_appointments.tenant_id` -> `tenants.id`
- `service_appointments.assigned_technician_user_id` -> `users.id`

### Cotizaciones
- `quotes.ticket_id` -> `tickets.id`
- `quotes.lead_id` -> `leads.id`
- `quotes.tenant_id` -> `tenants.id`
- `quotes.customer_user_id` -> `users.id`
- `quote_items.quote_id` -> `quotes.id`
- `quote_items.listing_id` -> `listings.id`
- `quote_status_history.quote_id` -> `quotes.id`
- `quote_status_history.changed_by_user_id` -> `users.id`

### IA y compatibilidad
- `listing_components.listing_id` -> `listings.id`
- `listing_components.component_type_id` -> `technical_component_types.id`
- `compatibility_rules.source_component_type_id` -> `technical_component_types.id`
- `compatibility_rules.target_component_type_id` -> `technical_component_types.id`
- `pc_builds.user_id` -> `users.id`
- `pc_build_items.pc_build_id` -> `pc_builds.id`
- `pc_build_items.listing_id` -> `listings.id`
- `pc_build_items.component_type_id` -> `technical_component_types.id`
- `intent_queries.user_id` -> `users.id`

### Social
- `reviews.tenant_id` -> `tenants.id`
- `reviews.user_id` -> `users.id`
- `reviews.ticket_id` -> `tickets.id`
- `reviews.quote_id` -> `quotes.id`
- `feed_posts.tenant_id` -> `tenants.id`
- `feed_posts.author_user_id` -> `users.id`
- `feed_media.feed_post_id` -> `feed_posts.id`
- `post_comments.feed_post_id` -> `feed_posts.id`
- `post_comments.user_id` -> `users.id`

### Notificaciones y eventos
- `notifications.user_id` -> `users.id`
- `system_events.tenant_id` -> `tenants.id`
- `notification_preferences.user_id` -> `users.id`
- `notification_channels.user_id` -> `users.id`

### Monetización
- `tenant_subscriptions.tenant_id` -> `tenants.id`
- `tenant_subscriptions.subscription_plan_id` -> `subscription_plans.id`
- `invoices.tenant_id` -> `tenants.id`
- `invoices.tenant_subscription_id` -> `tenant_subscriptions.id`
- `invoice_items.invoice_id` -> `invoices.id`
- `performance_charges.tenant_id` -> `tenants.id`
- `billing_disputes.tenant_id` -> `tenants.id`
- `billing_disputes.invoice_id` -> `invoices.id`
- `billing_disputes.performance_charge_id` -> `performance_charges.id`
- `billing_disputes.resolved_by_user_id` -> `users.id`
- `ledger_entries.tenant_id` -> `tenants.id`
- `ledger_entries.ambassador_id` -> `ambassadors.id`
- `payments.invoice_id` -> `invoices.id`
- `payments.tenant_id` -> `tenants.id`

### Embajadores
- `ambassadors.user_id` -> `users.id`
- `ambassador_referrals.ambassador_id` -> `ambassadors.id`
- `ambassador_referrals.tenant_id` -> `tenants.id`
- `ambassador_commissions.ambassador_id` -> `ambassadors.id`
- `ambassador_commissions.ambassador_referral_id` -> `ambassador_referrals.id`
- `ambassador_commissions.commission_rule_id` -> `commission_rules.id`
- `payout_items.payout_cycle_id` -> `payout_cycles.id`
- `payout_items.ambassador_id` -> `ambassadors.id`
- `payout_items.ambassador_commission_id` -> `ambassador_commissions.id`

### Moderación, auditoría y métricas
- `moderation_actions.performed_by_user_id` -> `users.id`
- `audit_logs.user_id` -> `users.id`
- `audit_logs.tenant_id` -> `tenants.id`
- `tenant_daily_metrics.tenant_id` -> `tenants.id`

---

## 17. Constraints e índices añadidos

### Unique
- `users.email`
- `roles.name`
- `permissions.code`
- `tenants.tax_id`
- `business_categories.name`
- `catalog_categories.slug`
- `brands.slug`
- `ambassadors.referral_code`

### Check
- `reviews.rating` entre 1 y 5
- `quotes.total_amount >= 0`
- `invoices.total_amount >= 0`
- `payments.amount >= 0`

### Índices de apoyo
- Índices en claves relacionales principales y columnas de filtro frecuente (`tenant_id`, `status`, `created_at`, `is_read`)

