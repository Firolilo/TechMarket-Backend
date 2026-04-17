CREATE TABLE IF NOT EXISTS notification_preferences (
    id UUID PRIMARY KEY,
    user_id UUID,
    notification_type VARCHAR(255),
    email_enabled VARCHAR(255),
    push_enabled VARCHAR(255),
    whatsapp_enabled VARCHAR(255),
    in_app_enabled VARCHAR(255),
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);
