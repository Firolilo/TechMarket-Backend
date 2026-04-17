CREATE TABLE IF NOT EXISTS quotes (
    id UUID PRIMARY KEY,
    ticket_id UUID,
    lead_id UUID,
    tenant_id UUID,
    customer_user_id UUID,
    status VARCHAR(255),
    subtotal_amount NUMERIC(14,2),
    discount_amount NUMERIC(14,2),
    tax_amount NUMERIC(14,2),
    total_amount NUMERIC(14,2),
    currency VARCHAR(255),
    valid_until VARCHAR(255),
    created_at TIMESTAMPTZ,
    approved_at TIMESTAMPTZ
);
