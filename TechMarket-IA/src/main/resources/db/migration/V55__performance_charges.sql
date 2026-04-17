CREATE TABLE IF NOT EXISTS performance_charges (
    id UUID PRIMARY KEY,
    tenant_id UUID,
    reference_type VARCHAR(255),
    reference_id UUID,
    charge_type VARCHAR(255),
    amount VARCHAR(255),
    status VARCHAR(255),
    generated_at TIMESTAMPTZ
);
