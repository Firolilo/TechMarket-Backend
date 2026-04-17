CREATE TABLE IF NOT EXISTS pc_builds (
    id UUID PRIMARY KEY,
    user_id UUID,
    name VARCHAR(255),
    description VARCHAR(255),
    status VARCHAR(255),
    is_shareable BOOLEAN,
    created_at TIMESTAMPTZ
);
