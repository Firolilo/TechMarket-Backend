CREATE TABLE IF NOT EXISTS moderation_actions (
    id UUID PRIMARY KEY,
    entity_type VARCHAR(255),
    entity_id UUID,
    action_type VARCHAR(255),
    reason VARCHAR(255),
    performed_by_user_id UUID,
    created_at TIMESTAMPTZ
);
