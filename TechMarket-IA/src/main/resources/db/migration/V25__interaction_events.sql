CREATE TABLE IF NOT EXISTS interaction_events (
    id UUID PRIMARY KEY,
    user_id UUID,
    tenant_id UUID,
    listing_id UUID,
    event_type VARCHAR(255),
    source_channel VARCHAR(255),
    metadata_json JSONB,
    created_at TIMESTAMPTZ
);
