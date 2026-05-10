ALTER TABLE ambassadors ADD COLUMN IF NOT EXISTS sponsor_ambassador_id UUID;

CREATE INDEX IF NOT EXISTS idx_ambassadors_sponsor_ambassador_id
    ON ambassadors (sponsor_ambassador_id);

CREATE TABLE IF NOT EXISTS ambassador_payout_methods (
    id UUID PRIMARY KEY,
    ambassador_id UUID NOT NULL,
    method_type VARCHAR(255) NOT NULL,
    bank VARCHAR(255),
    account_number VARCHAR(255),
    holder_name VARCHAR(255),
    last4 VARCHAR(4),
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_ambassador_payout_methods_ambassador_id
    ON ambassador_payout_methods (ambassador_id);

CREATE TABLE IF NOT EXISTS ambassador_invitations (
    id UUID PRIMARY KEY,
    ambassador_id UUID NOT NULL,
    email VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    phone VARCHAR(50),
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_ambassador_invitations_ambassador_id
    ON ambassador_invitations (ambassador_id);
