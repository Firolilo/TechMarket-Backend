CREATE TABLE IF NOT EXISTS client_addresses (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    title VARCHAR(255),
    country VARCHAR(255),
    city VARCHAR(255),
    address VARCHAR(255),
    reference VARCHAR(255),
    default_address BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_client_addresses_user_id ON client_addresses (user_id);
