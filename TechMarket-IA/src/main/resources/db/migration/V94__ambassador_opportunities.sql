CREATE TABLE IF NOT EXISTS ambassador_opportunities (
    id              UUID PRIMARY KEY,
    ambassador_id   UUID NOT NULL,
    opportunity_type VARCHAR(32) NOT NULL,
    zone            VARCHAR(255),
    description     TEXT,
    potential       VARCHAR(32) NOT NULL DEFAULT 'medio',
    data_source     VARCHAR(255),
    status          VARCHAR(32) NOT NULL DEFAULT 'nueva',
    is_saved        BOOLEAN NOT NULL DEFAULT FALSE,
    detected_at     TIMESTAMPTZ,
    updated_at      TIMESTAMPTZ
);
