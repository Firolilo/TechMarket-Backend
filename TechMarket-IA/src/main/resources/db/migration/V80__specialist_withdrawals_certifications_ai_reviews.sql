ALTER TABLE reviews
    ADD COLUMN IF NOT EXISTS technician_response VARCHAR(255),
    ADD COLUMN IF NOT EXISTS technician_response_at TIMESTAMPTZ;

CREATE TABLE IF NOT EXISTS specialist_withdrawals (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    amount NUMERIC(14,2) NOT NULL,
    currency VARCHAR(16) NOT NULL DEFAULT 'Bs',
    status VARCHAR(64) NOT NULL,
    requested_at TIMESTAMPTZ,
    estimated_at DATE
);

CREATE TABLE IF NOT EXISTS specialist_certifications (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    institution VARCHAR(255),
    obtained_at VARCHAR(32),
    file_url VARCHAR(255),
    status VARCHAR(64) NOT NULL,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS specialist_ai_queries (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    query_text VARCHAR(255) NOT NULL,
    focus VARCHAR(64),
    response_summary VARCHAR(255),
    created_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_specialist_withdrawals_user_id
    ON specialist_withdrawals (user_id);
CREATE INDEX IF NOT EXISTS idx_specialist_certifications_user_id
    ON specialist_certifications (user_id);
CREATE INDEX IF NOT EXISTS idx_specialist_ai_queries_user_id
    ON specialist_ai_queries (user_id);
