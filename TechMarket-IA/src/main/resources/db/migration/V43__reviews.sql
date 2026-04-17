CREATE TABLE IF NOT EXISTS reviews (
    id UUID PRIMARY KEY,
    tenant_id UUID,
    user_id UUID,
    ticket_id UUID,
    quote_id UUID,
    rating NUMERIC(3,2),
    comment VARCHAR(255),
    moderation_status VARCHAR(255),
    created_at TIMESTAMPTZ
);
