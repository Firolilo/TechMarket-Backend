-- Add relational integrity and performance indexes for TechMarket schema

-- users / roles / permissions
ALTER TABLE user_roles
    ADD CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id),
    ADD CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles (id);

ALTER TABLE role_permissions
    ADD CONSTRAINT fk_role_permissions_role FOREIGN KEY (role_id) REFERENCES roles (id),
    ADD CONSTRAINT fk_role_permissions_permission FOREIGN KEY (permission_id) REFERENCES permissions (id);

-- tenant core
ALTER TABLE tenant_members
    ADD CONSTRAINT fk_tenant_members_tenant FOREIGN KEY (tenant_id) REFERENCES tenants (id),
    ADD CONSTRAINT fk_tenant_members_user FOREIGN KEY (user_id) REFERENCES users (id),
    ADD CONSTRAINT fk_tenant_members_invited_by FOREIGN KEY (invited_by_user_id) REFERENCES users (id);

ALTER TABLE branches
    ADD CONSTRAINT fk_branches_tenant FOREIGN KEY (tenant_id) REFERENCES tenants (id);

ALTER TABLE tenant_verifications
    ADD CONSTRAINT fk_tenant_verifications_tenant FOREIGN KEY (tenant_id) REFERENCES tenants (id),
    ADD CONSTRAINT fk_tenant_verifications_reviewed_by FOREIGN KEY (reviewed_by_user_id) REFERENCES users (id);

ALTER TABLE tenant_verification_documents
    ADD CONSTRAINT fk_tenant_verification_documents_verification FOREIGN KEY (tenant_verification_id) REFERENCES tenant_verifications (id),
    ADD CONSTRAINT fk_tenant_verification_documents_uploaded_by FOREIGN KEY (uploaded_by_user_id) REFERENCES users (id);

ALTER TABLE tenant_settings
    ADD CONSTRAINT fk_tenant_settings_tenant FOREIGN KEY (tenant_id) REFERENCES tenants (id);

ALTER TABLE tenant_profiles
    ADD CONSTRAINT fk_tenant_profiles_tenant FOREIGN KEY (tenant_id) REFERENCES tenants (id);

-- catalog / listings
ALTER TABLE listings
    ADD CONSTRAINT fk_listings_tenant FOREIGN KEY (tenant_id) REFERENCES tenants (id),
    ADD CONSTRAINT fk_listings_category FOREIGN KEY (category_id) REFERENCES catalog_categories (id),
    ADD CONSTRAINT fk_listings_brand FOREIGN KEY (brand_id) REFERENCES brands (id);

ALTER TABLE branch_listings
    ADD CONSTRAINT fk_branch_listings_branch FOREIGN KEY (branch_id) REFERENCES branches (id),
    ADD CONSTRAINT fk_branch_listings_listing FOREIGN KEY (listing_id) REFERENCES listings (id);

ALTER TABLE listing_images
    ADD CONSTRAINT fk_listing_images_listing FOREIGN KEY (listing_id) REFERENCES listings (id);

ALTER TABLE listing_specifications
    ADD CONSTRAINT fk_listing_specifications_listing FOREIGN KEY (listing_id) REFERENCES listings (id);

ALTER TABLE branch_inventory
    ADD CONSTRAINT fk_branch_inventory_listing FOREIGN KEY (listing_id) REFERENCES listings (id),
    ADD CONSTRAINT fk_branch_inventory_branch FOREIGN KEY (branch_id) REFERENCES branches (id);

ALTER TABLE service_details
    ADD CONSTRAINT fk_service_details_listing FOREIGN KEY (listing_id) REFERENCES listings (id);

-- discovery and interaction
ALTER TABLE favorites
    ADD CONSTRAINT fk_favorites_user FOREIGN KEY (user_id) REFERENCES users (id),
    ADD CONSTRAINT fk_favorites_tenant FOREIGN KEY (tenant_id) REFERENCES tenants (id),
    ADD CONSTRAINT fk_favorites_listing FOREIGN KEY (listing_id) REFERENCES listings (id);

ALTER TABLE search_history
    ADD CONSTRAINT fk_search_history_user FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE interaction_events
    ADD CONSTRAINT fk_interaction_events_user FOREIGN KEY (user_id) REFERENCES users (id),
    ADD CONSTRAINT fk_interaction_events_tenant FOREIGN KEY (tenant_id) REFERENCES tenants (id),
    ADD CONSTRAINT fk_interaction_events_listing FOREIGN KEY (listing_id) REFERENCES listings (id);

-- leads
ALTER TABLE leads
    ADD CONSTRAINT fk_leads_tenant FOREIGN KEY (tenant_id) REFERENCES tenants (id),
    ADD CONSTRAINT fk_leads_branch FOREIGN KEY (branch_id) REFERENCES branches (id),
    ADD CONSTRAINT fk_leads_user FOREIGN KEY (user_id) REFERENCES users (id),
    ADD CONSTRAINT fk_leads_listing FOREIGN KEY (listing_id) REFERENCES listings (id);

ALTER TABLE lead_events
    ADD CONSTRAINT fk_lead_events_lead FOREIGN KEY (lead_id) REFERENCES leads (id),
    ADD CONSTRAINT fk_lead_events_performed_by FOREIGN KEY (performed_by_user_id) REFERENCES users (id);

ALTER TABLE lead_assignments
    ADD CONSTRAINT fk_lead_assignments_lead FOREIGN KEY (lead_id) REFERENCES leads (id),
    ADD CONSTRAINT fk_lead_assignments_assigned_to FOREIGN KEY (assigned_to_user_id) REFERENCES users (id);

-- tickets / operations
ALTER TABLE tickets
    ADD CONSTRAINT fk_tickets_tenant FOREIGN KEY (tenant_id) REFERENCES tenants (id),
    ADD CONSTRAINT fk_tickets_branch FOREIGN KEY (branch_id) REFERENCES branches (id),
    ADD CONSTRAINT fk_tickets_customer FOREIGN KEY (customer_user_id) REFERENCES users (id),
    ADD CONSTRAINT fk_tickets_lead FOREIGN KEY (lead_id) REFERENCES leads (id),
    ADD CONSTRAINT fk_tickets_listing FOREIGN KEY (listing_id) REFERENCES listings (id),
    ADD CONSTRAINT fk_tickets_assigned_user FOREIGN KEY (assigned_user_id) REFERENCES users (id),
    ADD CONSTRAINT fk_tickets_assigned_technician FOREIGN KEY (assigned_technician_user_id) REFERENCES users (id);

ALTER TABLE ticket_messages
    ADD CONSTRAINT fk_ticket_messages_ticket FOREIGN KEY (ticket_id) REFERENCES tickets (id),
    ADD CONSTRAINT fk_ticket_messages_author FOREIGN KEY (author_user_id) REFERENCES users (id);

ALTER TABLE ticket_attachments
    ADD CONSTRAINT fk_ticket_attachments_ticket FOREIGN KEY (ticket_id) REFERENCES tickets (id),
    ADD CONSTRAINT fk_ticket_attachments_message FOREIGN KEY (ticket_message_id) REFERENCES ticket_messages (id),
    ADD CONSTRAINT fk_ticket_attachments_uploaded_by FOREIGN KEY (uploaded_by_user_id) REFERENCES users (id);

ALTER TABLE ticket_status_history
    ADD CONSTRAINT fk_ticket_status_history_ticket FOREIGN KEY (ticket_id) REFERENCES tickets (id),
    ADD CONSTRAINT fk_ticket_status_history_changed_by FOREIGN KEY (changed_by_user_id) REFERENCES users (id);

ALTER TABLE service_appointments
    ADD CONSTRAINT fk_service_appointments_ticket FOREIGN KEY (ticket_id) REFERENCES tickets (id),
    ADD CONSTRAINT fk_service_appointments_tenant FOREIGN KEY (tenant_id) REFERENCES tenants (id),
    ADD CONSTRAINT fk_service_appointments_technician FOREIGN KEY (assigned_technician_user_id) REFERENCES users (id);

-- quotes
ALTER TABLE quotes
    ADD CONSTRAINT fk_quotes_ticket FOREIGN KEY (ticket_id) REFERENCES tickets (id),
    ADD CONSTRAINT fk_quotes_lead FOREIGN KEY (lead_id) REFERENCES leads (id),
    ADD CONSTRAINT fk_quotes_tenant FOREIGN KEY (tenant_id) REFERENCES tenants (id),
    ADD CONSTRAINT fk_quotes_customer FOREIGN KEY (customer_user_id) REFERENCES users (id);

ALTER TABLE quote_items
    ADD CONSTRAINT fk_quote_items_quote FOREIGN KEY (quote_id) REFERENCES quotes (id),
    ADD CONSTRAINT fk_quote_items_listing FOREIGN KEY (listing_id) REFERENCES listings (id);

ALTER TABLE quote_status_history
    ADD CONSTRAINT fk_quote_status_history_quote FOREIGN KEY (quote_id) REFERENCES quotes (id),
    ADD CONSTRAINT fk_quote_status_history_changed_by FOREIGN KEY (changed_by_user_id) REFERENCES users (id);

-- AI compatibility
ALTER TABLE listing_components
    ADD CONSTRAINT fk_listing_components_listing FOREIGN KEY (listing_id) REFERENCES listings (id),
    ADD CONSTRAINT fk_listing_components_component_type FOREIGN KEY (component_type_id) REFERENCES technical_component_types (id);

ALTER TABLE compatibility_rules
    ADD CONSTRAINT fk_compatibility_rules_source_type FOREIGN KEY (source_component_type_id) REFERENCES technical_component_types (id),
    ADD CONSTRAINT fk_compatibility_rules_target_type FOREIGN KEY (target_component_type_id) REFERENCES technical_component_types (id);

ALTER TABLE pc_builds
    ADD CONSTRAINT fk_pc_builds_user FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE pc_build_items
    ADD CONSTRAINT fk_pc_build_items_build FOREIGN KEY (pc_build_id) REFERENCES pc_builds (id),
    ADD CONSTRAINT fk_pc_build_items_listing FOREIGN KEY (listing_id) REFERENCES listings (id),
    ADD CONSTRAINT fk_pc_build_items_component_type FOREIGN KEY (component_type_id) REFERENCES technical_component_types (id);

ALTER TABLE intent_queries
    ADD CONSTRAINT fk_intent_queries_user FOREIGN KEY (user_id) REFERENCES users (id);

-- social
ALTER TABLE reviews
    ADD CONSTRAINT fk_reviews_tenant FOREIGN KEY (tenant_id) REFERENCES tenants (id),
    ADD CONSTRAINT fk_reviews_user FOREIGN KEY (user_id) REFERENCES users (id),
    ADD CONSTRAINT fk_reviews_ticket FOREIGN KEY (ticket_id) REFERENCES tickets (id),
    ADD CONSTRAINT fk_reviews_quote FOREIGN KEY (quote_id) REFERENCES quotes (id);

ALTER TABLE feed_posts
    ADD CONSTRAINT fk_feed_posts_tenant FOREIGN KEY (tenant_id) REFERENCES tenants (id),
    ADD CONSTRAINT fk_feed_posts_author FOREIGN KEY (author_user_id) REFERENCES users (id);

ALTER TABLE feed_media
    ADD CONSTRAINT fk_feed_media_post FOREIGN KEY (feed_post_id) REFERENCES feed_posts (id);

ALTER TABLE post_comments
    ADD CONSTRAINT fk_post_comments_post FOREIGN KEY (feed_post_id) REFERENCES feed_posts (id),
    ADD CONSTRAINT fk_post_comments_user FOREIGN KEY (user_id) REFERENCES users (id);

-- notifications/events
ALTER TABLE notifications
    ADD CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE system_events
    ADD CONSTRAINT fk_system_events_tenant FOREIGN KEY (tenant_id) REFERENCES tenants (id);

ALTER TABLE notification_preferences
    ADD CONSTRAINT fk_notification_preferences_user FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE notification_channels
    ADD CONSTRAINT fk_notification_channels_user FOREIGN KEY (user_id) REFERENCES users (id);

-- billing
ALTER TABLE tenant_subscriptions
    ADD CONSTRAINT fk_tenant_subscriptions_tenant FOREIGN KEY (tenant_id) REFERENCES tenants (id),
    ADD CONSTRAINT fk_tenant_subscriptions_plan FOREIGN KEY (subscription_plan_id) REFERENCES subscription_plans (id);

ALTER TABLE invoices
    ADD CONSTRAINT fk_invoices_tenant FOREIGN KEY (tenant_id) REFERENCES tenants (id),
    ADD CONSTRAINT fk_invoices_subscription FOREIGN KEY (tenant_subscription_id) REFERENCES tenant_subscriptions (id);

ALTER TABLE invoice_items
    ADD CONSTRAINT fk_invoice_items_invoice FOREIGN KEY (invoice_id) REFERENCES invoices (id);

ALTER TABLE performance_charges
    ADD CONSTRAINT fk_performance_charges_tenant FOREIGN KEY (tenant_id) REFERENCES tenants (id);

ALTER TABLE billing_disputes
    ADD CONSTRAINT fk_billing_disputes_tenant FOREIGN KEY (tenant_id) REFERENCES tenants (id),
    ADD CONSTRAINT fk_billing_disputes_invoice FOREIGN KEY (invoice_id) REFERENCES invoices (id),
    ADD CONSTRAINT fk_billing_disputes_performance_charge FOREIGN KEY (performance_charge_id) REFERENCES performance_charges (id),
    ADD CONSTRAINT fk_billing_disputes_resolved_by FOREIGN KEY (resolved_by_user_id) REFERENCES users (id);

ALTER TABLE ledger_entries
    ADD CONSTRAINT fk_ledger_entries_tenant FOREIGN KEY (tenant_id) REFERENCES tenants (id),
    ADD CONSTRAINT fk_ledger_entries_ambassador FOREIGN KEY (ambassador_id) REFERENCES ambassadors (id);

ALTER TABLE payments
    ADD CONSTRAINT fk_payments_invoice FOREIGN KEY (invoice_id) REFERENCES invoices (id),
    ADD CONSTRAINT fk_payments_tenant FOREIGN KEY (tenant_id) REFERENCES tenants (id);

-- ambassadors
ALTER TABLE ambassadors
    ADD CONSTRAINT fk_ambassadors_user FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE ambassador_referrals
    ADD CONSTRAINT fk_ambassador_referrals_ambassador FOREIGN KEY (ambassador_id) REFERENCES ambassadors (id),
    ADD CONSTRAINT fk_ambassador_referrals_tenant FOREIGN KEY (tenant_id) REFERENCES tenants (id);

ALTER TABLE ambassador_commissions
    ADD CONSTRAINT fk_ambassador_commissions_ambassador FOREIGN KEY (ambassador_id) REFERENCES ambassadors (id),
    ADD CONSTRAINT fk_ambassador_commissions_referral FOREIGN KEY (ambassador_referral_id) REFERENCES ambassador_referrals (id),
    ADD CONSTRAINT fk_ambassador_commissions_rule FOREIGN KEY (commission_rule_id) REFERENCES commission_rules (id);

ALTER TABLE payout_items
    ADD CONSTRAINT fk_payout_items_cycle FOREIGN KEY (payout_cycle_id) REFERENCES payout_cycles (id),
    ADD CONSTRAINT fk_payout_items_ambassador FOREIGN KEY (ambassador_id) REFERENCES ambassadors (id),
    ADD CONSTRAINT fk_payout_items_commission FOREIGN KEY (ambassador_commission_id) REFERENCES ambassador_commissions (id);

-- fraud/moderation/audit
ALTER TABLE moderation_actions
    ADD CONSTRAINT fk_moderation_actions_performed_by FOREIGN KEY (performed_by_user_id) REFERENCES users (id);

ALTER TABLE audit_logs
    ADD CONSTRAINT fk_audit_logs_user FOREIGN KEY (user_id) REFERENCES users (id),
    ADD CONSTRAINT fk_audit_logs_tenant FOREIGN KEY (tenant_id) REFERENCES tenants (id);

-- metrics
ALTER TABLE tenant_daily_metrics
    ADD CONSTRAINT fk_tenant_daily_metrics_tenant FOREIGN KEY (tenant_id) REFERENCES tenants (id);

-- uniqueness and business safeguards
ALTER TABLE users ADD CONSTRAINT uq_users_email UNIQUE (email);
ALTER TABLE roles ADD CONSTRAINT uq_roles_name UNIQUE (name);
ALTER TABLE permissions ADD CONSTRAINT uq_permissions_code UNIQUE (code);
ALTER TABLE tenants ADD CONSTRAINT uq_tenants_tax_id UNIQUE (tax_id);
ALTER TABLE business_categories ADD CONSTRAINT uq_business_categories_name UNIQUE (name);
ALTER TABLE catalog_categories ADD CONSTRAINT uq_catalog_categories_slug UNIQUE (slug);
ALTER TABLE brands ADD CONSTRAINT uq_brands_slug UNIQUE (slug);
ALTER TABLE ambassadors ADD CONSTRAINT uq_ambassadors_referral_code UNIQUE (referral_code);

ALTER TABLE reviews ADD CONSTRAINT chk_reviews_rating_range CHECK (rating >= 1 AND rating <= 5);
ALTER TABLE quotes ADD CONSTRAINT chk_quotes_total_non_negative CHECK (total_amount >= 0);
ALTER TABLE invoices ADD CONSTRAINT chk_invoices_total_non_negative CHECK (total_amount >= 0);
ALTER TABLE payments ADD CONSTRAINT chk_payments_amount_non_negative CHECK (amount >= 0);

-- indexes for common lookup columns
CREATE INDEX idx_user_roles_user_id ON user_roles (user_id);
CREATE INDEX idx_user_roles_role_id ON user_roles (role_id);
CREATE INDEX idx_role_permissions_role_id ON role_permissions (role_id);
CREATE INDEX idx_role_permissions_permission_id ON role_permissions (permission_id);
CREATE INDEX idx_tenant_members_tenant_id ON tenant_members (tenant_id);
CREATE INDEX idx_branches_tenant_id ON branches (tenant_id);
CREATE INDEX idx_listings_tenant_id ON listings (tenant_id);
CREATE INDEX idx_tickets_tenant_id ON tickets (tenant_id);
CREATE INDEX idx_tickets_status ON tickets (status);
CREATE INDEX idx_quotes_tenant_id ON quotes (tenant_id);
CREATE INDEX idx_quotes_status ON quotes (status);
CREATE INDEX idx_leads_tenant_id ON leads (tenant_id);
CREATE INDEX idx_leads_status ON leads (status);
CREATE INDEX idx_interaction_events_created_at ON interaction_events (created_at);
CREATE INDEX idx_notifications_user_id ON notifications (user_id);
CREATE INDEX idx_notifications_is_read ON notifications (is_read);
CREATE INDEX idx_feed_posts_tenant_id ON feed_posts (tenant_id);
CREATE INDEX idx_reviews_tenant_id ON reviews (tenant_id);
CREATE INDEX idx_payments_tenant_id ON payments (tenant_id);
CREATE INDEX idx_audit_logs_tenant_id ON audit_logs (tenant_id);
CREATE INDEX idx_tenant_daily_metrics_tenant_date ON tenant_daily_metrics (tenant_id, metric_date);
