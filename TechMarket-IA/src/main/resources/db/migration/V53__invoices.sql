CREATE TABLE IF NOT EXISTS invoices (
    id UUID PRIMARY KEY,
    tenant_id UUID,
    tenant_subscription_id UUID,
    invoice_number VARCHAR(255),
    subtotal_amount NUMERIC(14,2),
    tax_amount NUMERIC(14,2),
    total_amount NUMERIC(14,2),
    currency VARCHAR(255),
    status VARCHAR(255),
    issued_at TIMESTAMPTZ,
    due_at TIMESTAMPTZ
);
