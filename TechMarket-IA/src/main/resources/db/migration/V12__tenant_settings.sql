CREATE TABLE IF NOT EXISTS tenant_settings (
    id UUID PRIMARY KEY,
    tenant_id UUID,
    currency VARCHAR(255),
    language VARCHAR(255),
    time_zone VARCHAR(255),
    whatsapp_number VARCHAR(255),
    accepts_quotes BOOLEAN,
    accepts_tickets BOOLEAN,
    settings_json JSONB
);
