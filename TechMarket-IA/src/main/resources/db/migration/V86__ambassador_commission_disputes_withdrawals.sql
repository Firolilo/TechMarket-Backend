CREATE TABLE IF NOT EXISTS ambassador_commission_disputes (
    id UUID PRIMARY KEY,
    ambassador_id UUID NOT NULL,
    ambassador_commission_id UUID NOT NULL,
    reason VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_ambassador_commission_disputes_ambassador_id
    ON ambassador_commission_disputes (ambassador_id);

CREATE TABLE IF NOT EXISTS ambassador_withdrawals (
    id UUID PRIMARY KEY,
    ambassador_id UUID NOT NULL,
    amount NUMERIC(14,2) NOT NULL,
    currency VARCHAR(16) NOT NULL DEFAULT 'Bs',
    status VARCHAR(255) NOT NULL,
    requested_at TIMESTAMPTZ,
    estimated_at DATE
);

CREATE INDEX IF NOT EXISTS idx_ambassador_withdrawals_ambassador_id
    ON ambassador_withdrawals (ambassador_id);
