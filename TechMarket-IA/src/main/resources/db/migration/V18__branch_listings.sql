CREATE TABLE IF NOT EXISTS branch_listings (
    id UUID PRIMARY KEY,
    branch_id UUID,
    listing_id UUID,
    status VARCHAR(255),
    is_available BOOLEAN,
    created_at TIMESTAMPTZ
);
