ALTER TABLE feed_posts ADD COLUMN IF NOT EXISTS community_id UUID;
ALTER TABLE notifications ADD COLUMN IF NOT EXISTS link_url VARCHAR(255);

CREATE INDEX IF NOT EXISTS idx_feed_posts_community_id ON feed_posts (community_id);
CREATE INDEX IF NOT EXISTS idx_notifications_user_sent_at ON notifications (user_id, sent_at);
