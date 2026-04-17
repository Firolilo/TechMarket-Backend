CREATE TABLE IF NOT EXISTS feed_media (
    id UUID PRIMARY KEY,
    feed_post_id UUID,
    media_url VARCHAR(255),
    media_type VARCHAR(255),
    display_order VARCHAR(255)
);
