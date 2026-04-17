CREATE TABLE IF NOT EXISTS lead_assignments (
    id UUID PRIMARY KEY,
    lead_id UUID,
    assigned_to_user_id UUID,
    assigned_at TIMESTAMPTZ,
    status VARCHAR(255)
);
