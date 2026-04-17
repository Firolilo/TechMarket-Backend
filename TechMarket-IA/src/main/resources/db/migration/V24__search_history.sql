CREATE TABLE IF NOT EXISTS search_history (
    id UUID PRIMARY KEY,
    user_id UUID,
    query_text VARCHAR(255),
    search_type VARCHAR(255),
    latitude NUMERIC(9,6),
    longitude NUMERIC(9,6),
    filters_json JSONB,
    created_at TIMESTAMPTZ
);
