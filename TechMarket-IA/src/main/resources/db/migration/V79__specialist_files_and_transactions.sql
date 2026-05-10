CREATE TABLE IF NOT EXISTS specialist_files (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    file_url VARCHAR(255) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(255),
    file_size VARCHAR(64),
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);

ALTER TABLE ticket_attachments
    ADD COLUMN IF NOT EXISTS file_size VARCHAR(64);

CREATE TABLE IF NOT EXISTS specialist_transactions (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    service_appointment_id UUID,
    service_name VARCHAR(255),
    client_name VARCHAR(255),
    amount NUMERIC(14,2) NOT NULL,
    platform_commission NUMERIC(14,2) NOT NULL DEFAULT 0,
    currency VARCHAR(16) NOT NULL DEFAULT 'Bs',
    status VARCHAR(64) NOT NULL,
    transaction_date TIMESTAMPTZ,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_specialist_files_user_id ON specialist_files (user_id);
CREATE INDEX IF NOT EXISTS idx_specialist_transactions_user_date
    ON specialist_transactions (user_id, transaction_date);
