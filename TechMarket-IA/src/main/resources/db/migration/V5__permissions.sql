CREATE TABLE IF NOT EXISTS permissions (
    id UUID PRIMARY KEY,
    code VARCHAR(255),
    name VARCHAR(255),
    description VARCHAR(255)
);
