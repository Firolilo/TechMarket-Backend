CREATE TABLE IF NOT EXISTS search_history (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    query_text VARCHAR(255) NOT NULL,
    result_type VARCHAR(64),
    searched_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS search_trends (
    id UUID PRIMARY KEY,
    query_text VARCHAR(255) NOT NULL,
    search_count INT NOT NULL DEFAULT 0,
    updated_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_search_history_user_date
    ON search_history (user_id, created_at);
CREATE INDEX IF NOT EXISTS idx_search_trends_count
    ON search_trends (search_count);
