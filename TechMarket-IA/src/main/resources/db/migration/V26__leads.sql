CREATE TABLE IF NOT EXISTS leads (
    id UUID PRIMARY KEY,
    tenant_id UUID,
    branch_id UUID,
    user_id UUID,
    listing_id UUID,
    source_channel VARCHAR(255),
    status VARCHAR(255),
    lead_score VARCHAR(255),
    notes VARCHAR(255),
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);
