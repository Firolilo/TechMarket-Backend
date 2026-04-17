CREATE TABLE IF NOT EXISTS commission_rules (
    id UUID PRIMARY KEY,
    event_type VARCHAR(255),
    level VARCHAR(255),
    fixed_amount NUMERIC(14,2),
    percentage_amount NUMERIC(14,2),
    is_active BOOLEAN,
    valid_from DATE,
    valid_to DATE
);
