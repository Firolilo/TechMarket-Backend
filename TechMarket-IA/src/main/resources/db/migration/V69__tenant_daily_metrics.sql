CREATE TABLE IF NOT EXISTS tenant_daily_metrics (
    id UUID PRIMARY KEY,
    tenant_id UUID,
    metric_date DATE,
    profile_views VARCHAR(255),
    listing_views VARCHAR(255),
    leads_count INT,
    tickets_count INT,
    quotes_count INT,
    conversions_count INT,
    revenue_amount NUMERIC(14,2),
    average_response_time_minutes INT
);
