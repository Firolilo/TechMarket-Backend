ALTER TABLE reviews ADD COLUMN IF NOT EXISTS company_response VARCHAR(255);
ALTER TABLE reviews ADD COLUMN IF NOT EXISTS company_response_at TIMESTAMPTZ;

CREATE INDEX IF NOT EXISTS idx_tickets_tenant_company_chat
    ON tickets (tenant_id, ticket_type, created_at);

CREATE INDEX IF NOT EXISTS idx_reviews_tenant_created
    ON reviews (tenant_id, created_at);
