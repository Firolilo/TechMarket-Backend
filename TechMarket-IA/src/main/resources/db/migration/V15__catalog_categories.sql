CREATE TABLE IF NOT EXISTS catalog_categories (
    id UUID PRIMARY KEY,
    name VARCHAR(255),
    parent_category_id UUID,
    item_type VARCHAR(255),
    slug VARCHAR(255)
);
