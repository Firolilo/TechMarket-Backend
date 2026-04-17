CREATE TABLE IF NOT EXISTS system_events (
    id UUID PRIMARY KEY,
    event_type VARCHAR(255),
    entity_type VARCHAR(255),
    entity_id UUID,
    tenant_id UUID,
    payload_json JSONB,
    created_at TIMESTAMPTZ
);
