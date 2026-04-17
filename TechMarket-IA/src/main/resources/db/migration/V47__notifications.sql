CREATE TABLE IF NOT EXISTS notifications (
    id UUID PRIMARY KEY,
    user_id UUID,
    notification_type VARCHAR(255),
    title VARCHAR(255),
    message VARCHAR(255),
    channel VARCHAR(255),
    is_read BOOLEAN,
    sent_at TIMESTAMPTZ
);
