-- Recreación idempotente de las tablas de V97. Si la V97 quedó registrada en el historial
-- de Flyway pero las tablas no existen físicamente (por reset parcial de la BD o un repair
-- que solo actualizó checksums), esta migración garantiza que las tablas estén creadas.

CREATE TABLE IF NOT EXISTS community_member_roles (
    id UUID PRIMARY KEY,
    community_id UUID NOT NULL,
    user_id UUID NOT NULL,
    role VARCHAR(50) NOT NULL,
    granted_at TIMESTAMPTZ,
    CONSTRAINT uk_community_member_roles UNIQUE (community_id, user_id)
);

CREATE INDEX IF NOT EXISTS idx_community_member_roles_community
    ON community_member_roles (community_id);

CREATE TABLE IF NOT EXISTS community_post_likes (
    id UUID PRIMARY KEY,
    post_id UUID NOT NULL,
    user_id UUID NOT NULL,
    created_at TIMESTAMPTZ,
    CONSTRAINT uk_community_post_likes UNIQUE (post_id, user_id)
);

CREATE INDEX IF NOT EXISTS idx_community_post_likes_post
    ON community_post_likes (post_id);
