CREATE TABLE IF NOT EXISTS ticket_attachments (
    id UUID PRIMARY KEY,
    ticket_id UUID,
    ticket_message_id UUID,
    file_url VARCHAR(255),
    file_type VARCHAR(255),
    original_file_name VARCHAR(255),
    uploaded_by_user_id UUID,
    created_at TIMESTAMPTZ
);
