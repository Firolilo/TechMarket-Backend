CREATE TABLE IF NOT EXISTS notification_channels (
    id UUID PRIMARY KEY,
    user_id UUID,
    channel_type VARCHAR(255),
    destination VARCHAR(255),
    is_verified BOOLEAN,
    is_active BOOLEAN,
    created_at TIMESTAMPTZ
);
