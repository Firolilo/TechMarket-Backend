CREATE TABLE IF NOT EXISTS fraud_cases (
    id UUID PRIMARY KEY,
    case_type VARCHAR(255),
    entity_type VARCHAR(255),
    entity_id UUID,
    risk_level VARCHAR(255),
    status VARCHAR(255),
    reason VARCHAR(255),
    created_at TIMESTAMPTZ,
    resolved_at TIMESTAMPTZ
);
