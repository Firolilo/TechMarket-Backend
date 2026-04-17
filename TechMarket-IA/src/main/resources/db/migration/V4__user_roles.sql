CREATE TABLE IF NOT EXISTS user_roles (
    id UUID PRIMARY KEY,
    user_id UUID,
    role_id UUID,
    is_active BOOLEAN,
    assigned_at TIMESTAMPTZ
);
