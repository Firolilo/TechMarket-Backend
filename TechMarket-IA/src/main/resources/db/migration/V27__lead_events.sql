CREATE TABLE IF NOT EXISTS lead_events (
    id UUID PRIMARY KEY,
    lead_id UUID,
    event_type VARCHAR(255),
    performed_by_user_id UUID,
    details VARCHAR(255),
    created_at TIMESTAMPTZ
);
