CREATE TABLE IF NOT EXISTS feed_posts (
    id UUID PRIMARY KEY,
    tenant_id UUID,
    author_user_id UUID,
    post_type VARCHAR(255),
    title VARCHAR(255),
    content VARCHAR(255),
    status VARCHAR(255),
    created_at TIMESTAMPTZ
);
