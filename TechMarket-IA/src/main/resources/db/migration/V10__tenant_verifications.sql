CREATE TABLE IF NOT EXISTS tenant_verifications (
    id UUID PRIMARY KEY,
    tenant_id UUID,
    verification_status VARCHAR(255),
    verification_type VARCHAR(255),
    reviewed_by_user_id UUID,
    review_notes VARCHAR(255),
    reviewed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ
);
