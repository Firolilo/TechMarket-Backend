CREATE TABLE IF NOT EXISTS platform_daily_metrics (
    id UUID PRIMARY KEY,
    metric_date DATE,
    active_users_count INT,
    active_tenants_count INT,
    total_leads_count INT,
    total_tickets_count INT,
    conversion_rate NUMERIC(5,2),
    total_revenue_amount NUMERIC(14,2)
);
