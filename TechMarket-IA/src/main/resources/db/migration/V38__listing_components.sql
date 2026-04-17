CREATE TABLE IF NOT EXISTS listing_components (
    id UUID PRIMARY KEY,
    listing_id UUID,
    component_type_id UUID
);
