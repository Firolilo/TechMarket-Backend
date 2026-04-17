CREATE TABLE IF NOT EXISTS tenant_members (
    id UUID PRIMARY KEY,
    tenant_id UUID,
    user_id UUID,
    tenant_role VARCHAR(255),
    status VARCHAR(255),
    invited_by_user_id UUID,
    joined_at TIMESTAMPTZ
);
