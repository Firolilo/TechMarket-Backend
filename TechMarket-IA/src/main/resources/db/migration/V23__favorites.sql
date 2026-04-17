CREATE TABLE IF NOT EXISTS favorites (
    id UUID PRIMARY KEY,
    user_id UUID,
    tenant_id UUID,
    listing_id UUID,
    created_at TIMESTAMPTZ
);
