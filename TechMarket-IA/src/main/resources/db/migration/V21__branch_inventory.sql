CREATE TABLE IF NOT EXISTS branch_inventory (
    id UUID PRIMARY KEY,
    listing_id UUID,
    branch_id UUID,
    stock_available VARCHAR(255),
    stock_reserved VARCHAR(255),
    minimum_stock VARCHAR(255),
    updated_at TIMESTAMPTZ
);
