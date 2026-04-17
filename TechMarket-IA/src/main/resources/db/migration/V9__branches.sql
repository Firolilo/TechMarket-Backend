CREATE TABLE IF NOT EXISTS branches (
    id UUID PRIMARY KEY,
    tenant_id UUID,
    name VARCHAR(255),
    address VARCHAR(255),
    city VARCHAR(255),
    latitude NUMERIC(9,6),
    longitude NUMERIC(9,6),
    phone VARCHAR(255),
    opening_hours VARCHAR(255),
    is_main_branch BOOLEAN,
    status VARCHAR(255),
    created_at TIMESTAMPTZ
);
