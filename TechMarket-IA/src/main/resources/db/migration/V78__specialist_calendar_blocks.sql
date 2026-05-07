CREATE TABLE IF NOT EXISTS specialist_calendar_blocks (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    block_date DATE NOT NULL,
    start_time VARCHAR(16) NOT NULL,
    end_time VARCHAR(16),
    reason VARCHAR(255),
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_specialist_calendar_blocks_user_date
    ON specialist_calendar_blocks (user_id, block_date);
