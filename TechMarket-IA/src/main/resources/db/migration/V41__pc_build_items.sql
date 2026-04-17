CREATE TABLE IF NOT EXISTS pc_build_items (
    id UUID PRIMARY KEY,
    pc_build_id UUID,
    listing_id UUID,
    component_type_id UUID,
    quantity INT
);
