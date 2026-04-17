CREATE TABLE IF NOT EXISTS listing_specifications (
    id UUID PRIMARY KEY,
    listing_id UUID,
    attribute_name VARCHAR(255),
    attribute_value VARCHAR(255),
    unit VARCHAR(255),
    is_normalized BOOLEAN
);
