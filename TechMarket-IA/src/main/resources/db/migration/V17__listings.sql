CREATE TABLE IF NOT EXISTS listings (
    id UUID PRIMARY KEY,
    tenant_id UUID,
    category_id UUID,
    brand_id UUID,
    listing_type VARCHAR(255),
    title VARCHAR(255),
    description VARCHAR(255),
    base_price NUMERIC(14,2),
    currency VARCHAR(255),
    status VARCHAR(255),
    is_visible BOOLEAN,
    internal_sku VARCHAR(255),
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);
