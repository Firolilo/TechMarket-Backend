CREATE TABLE IF NOT EXISTS user_invoices (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    transaction_id UUID,
    invoice_number VARCHAR(255) NOT NULL,
    amount NUMERIC(14,2) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    download_url VARCHAR(500),
    created_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_user_invoices_user_id
    ON user_invoices (user_id);

CREATE TABLE IF NOT EXISTS user_payment_refunds (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    transaction_id UUID NOT NULL,
    reason VARCHAR(500),
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_user_payment_refunds_user_id
    ON user_payment_refunds (user_id);

CREATE TABLE IF NOT EXISTS user_reports (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    object_type VARCHAR(255) NOT NULL,
    object_id VARCHAR(255) NOT NULL,
    reason VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    status VARCHAR(255) NOT NULL,
    admin_action VARCHAR(255),
    created_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_user_reports_user_id
    ON user_reports (user_id);

CREATE INDEX IF NOT EXISTS idx_user_reports_status
    ON user_reports (status);
