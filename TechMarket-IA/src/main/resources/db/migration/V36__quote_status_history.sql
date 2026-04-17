CREATE TABLE IF NOT EXISTS quote_status_history (
    id UUID PRIMARY KEY,
    quote_id UUID,
    old_status VARCHAR(255),
    new_status VARCHAR(255),
    changed_by_user_id UUID,
    created_at TIMESTAMPTZ
);
