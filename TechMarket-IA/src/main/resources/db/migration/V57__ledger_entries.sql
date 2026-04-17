CREATE TABLE IF NOT EXISTS ledger_entries (
    id UUID PRIMARY KEY,
    tenant_id UUID,
    ambassador_id UUID,
    entry_type VARCHAR(255),
    reference_type VARCHAR(255),
    reference_id UUID,
    debit_amount NUMERIC(14,2),
    credit_amount NUMERIC(14,2),
    currency VARCHAR(255),
    description VARCHAR(255),
    created_at TIMESTAMPTZ
);
