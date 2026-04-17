CREATE TABLE IF NOT EXISTS ticket_messages (
    id UUID PRIMARY KEY,
    ticket_id UUID,
    author_user_id UUID,
    message_body VARCHAR(255),
    message_type VARCHAR(255),
    is_visible_to_customer BOOLEAN,
    created_at TIMESTAMPTZ
);
