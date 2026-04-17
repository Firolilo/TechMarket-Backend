CREATE TABLE IF NOT EXISTS post_comments (
    id UUID PRIMARY KEY,
    feed_post_id UUID,
    user_id UUID,
    comment_body VARCHAR(255),
    status VARCHAR(255),
    created_at TIMESTAMPTZ
);
