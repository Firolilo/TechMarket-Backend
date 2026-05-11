CREATE TABLE IF NOT EXISTS ambassador_leads (
    id UUID PRIMARY KEY,
    ambassador_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    lead_type VARCHAR(255),
    contact_name VARCHAR(255),
    phone VARCHAR(50),
    source VARCHAR(255),
    status VARCHAR(255) NOT NULL,
    close_probability INT NOT NULL DEFAULT 50,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_ambassador_leads_ambassador_id
    ON ambassador_leads (ambassador_id);
