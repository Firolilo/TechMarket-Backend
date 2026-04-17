CREATE TABLE IF NOT EXISTS technical_component_types (
    id UUID PRIMARY KEY,
    name VARCHAR(255),
    technical_group VARCHAR(255),
    description VARCHAR(255)
);
