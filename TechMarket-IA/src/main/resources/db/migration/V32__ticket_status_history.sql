CREATE TABLE IF NOT EXISTS ticket_status_history (
    id UUID PRIMARY KEY,
    ticket_id UUID,
    old_status VARCHAR(255),
    new_status VARCHAR(255),
    changed_by_user_id UUID,
    change_reason VARCHAR(255),
    created_at TIMESTAMPTZ
);
