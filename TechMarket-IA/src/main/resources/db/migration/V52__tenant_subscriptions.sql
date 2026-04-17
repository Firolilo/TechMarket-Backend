CREATE TABLE IF NOT EXISTS tenant_subscriptions (
    id UUID PRIMARY KEY,
    tenant_id UUID,
    subscription_plan_id UUID,
    status VARCHAR(255),
    start_date DATE,
    end_date DATE,
    auto_renew BOOLEAN
);
