CREATE TABLE IF NOT EXISTS payments (
    id UUID PRIMARY KEY,
    invoice_id UUID,
    tenant_id UUID,
    payment_method VARCHAR(255),
    payment_provider VARCHAR(255),
    external_reference VARCHAR(255),
    amount VARCHAR(255),
    currency VARCHAR(255),
    status VARCHAR(255),
    paid_at TIMESTAMPTZ
);
