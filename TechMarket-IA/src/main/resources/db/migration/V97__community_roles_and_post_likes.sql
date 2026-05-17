-- Roles dentro de comunidades (ADMIN, MODERATOR). Sin row = MEMBER.
-- Tabla nueva, no modifica tablas existentes.
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

-- Likes en posts de comunidad. Tabla nueva.
CREATE TABLE IF NOT EXISTS community_post_likes (
    id UUID PRIMARY KEY,
    post_id UUID NOT NULL,
    user_id UUID NOT NULL,
    created_at TIMESTAMPTZ,
    CONSTRAINT uk_community_post_likes UNIQUE (post_id, user_id)
);

CREATE INDEX IF NOT EXISTS idx_community_post_likes_post
    ON community_post_likes (post_id);
