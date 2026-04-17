CREATE TABLE IF NOT EXISTS system_alerts (
    id UUID PRIMARY KEY,
    alert_type VARCHAR(255),
    severity VARCHAR(255),
    source VARCHAR(255),
    description VARCHAR(255),
    status VARCHAR(255),
    created_at TIMESTAMPTZ,
    resolved_at TIMESTAMPTZ
);
