CREATE TABLE IF NOT EXISTS ambassador_referrals (
    id UUID PRIMARY KEY,
    ambassador_id UUID,
    tenant_id UUID,
    attribution_channel VARCHAR(255),
    used_code VARCHAR(255),
    status VARCHAR(255),
    created_at TIMESTAMPTZ
);
