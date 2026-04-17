CREATE TABLE IF NOT EXISTS audit_logs (
    id UUID PRIMARY KEY,
    user_id UUID,
    tenant_id UUID,
    action_name VARCHAR(255),
    entity_type VARCHAR(255),
    entity_id UUID,
    ip_address VARCHAR(255),
    user_agent VARCHAR(255),
    created_at TIMESTAMPTZ
);
