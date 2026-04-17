CREATE TABLE IF NOT EXISTS ambassadors (
    id UUID PRIMARY KEY,
    user_id UUID,
    referral_code VARCHAR(255),
    status VARCHAR(255),
    level VARCHAR(255),
    activated_at TIMESTAMPTZ
);
