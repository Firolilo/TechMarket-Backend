CREATE TABLE IF NOT EXISTS billing_disputes (
    id UUID PRIMARY KEY,
    tenant_id UUID,
    invoice_id UUID,
    performance_charge_id UUID,
    reason VARCHAR(255),
    status VARCHAR(255),
    resolved_by_user_id UUID,
    resolved_at TIMESTAMPTZ
);
