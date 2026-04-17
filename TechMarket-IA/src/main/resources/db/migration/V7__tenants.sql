CREATE TABLE IF NOT EXISTS tenants (
    id UUID PRIMARY KEY,
    business_name VARCHAR(255),
    legal_name VARCHAR(255),
    tax_id UUID,
    business_type VARCHAR(255),
    description VARCHAR(255),
    status VARCHAR(255),
    registered_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);
