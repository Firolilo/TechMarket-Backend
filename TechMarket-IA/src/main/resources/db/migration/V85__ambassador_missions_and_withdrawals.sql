CREATE TABLE IF NOT EXISTS ambassador_missions (
    id UUID PRIMARY KEY,
    ambassador_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    benefit VARCHAR(255),
    mission_type VARCHAR(64),
    priority VARCHAR(32) NOT NULL DEFAULT 'normal',
    status VARCHAR(32) NOT NULL DEFAULT 'disponible',
    steps TEXT,
    completion_criteria VARCHAR(255),
    progress NUMERIC(5,2),
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS ambassador_withdrawals (
    id UUID PRIMARY KEY,
    ambassador_id UUID NOT NULL,
    amount NUMERIC(14,2) NOT NULL,
    currency VARCHAR(16) NOT NULL DEFAULT 'Bs',
    status VARCHAR(64) NOT NULL,
    requested_at TIMESTAMPTZ,
    estimated_at DATE
);
