CREATE TABLE IF NOT EXISTS subscription_plans (
    id UUID PRIMARY KEY,
    name VARCHAR(255),
    description VARCHAR(255),
    price NUMERIC(14,2),
    currency VARCHAR(255),
    billing_period VARCHAR(255),
    benefits_json JSONB,
    is_active BOOLEAN
);
