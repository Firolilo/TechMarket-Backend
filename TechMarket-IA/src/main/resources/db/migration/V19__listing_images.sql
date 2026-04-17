CREATE TABLE IF NOT EXISTS listing_images (
    id UUID PRIMARY KEY,
    listing_id UUID,
    image_url VARCHAR(255),
    display_order VARCHAR(255),
    is_primary BOOLEAN
);
