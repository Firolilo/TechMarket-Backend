ALTER TABLE ambassadors ADD COLUMN IF NOT EXISTS country VARCHAR(255);
ALTER TABLE ambassadors ADD COLUMN IF NOT EXISTS city VARCHAR(255);
ALTER TABLE ambassadors ADD COLUMN IF NOT EXISTS avatar_url VARCHAR(500);
ALTER TABLE ambassadors ADD COLUMN IF NOT EXISTS description VARCHAR(1000);
ALTER TABLE ambassadors ADD COLUMN IF NOT EXISTS email_notifications BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE ambassadors ADD COLUMN IF NOT EXISTS push_notifications BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE ambassadors ADD COLUMN IF NOT EXISTS public_profile BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE ambassadors ADD COLUMN IF NOT EXISTS language VARCHAR(10) NOT NULL DEFAULT 'es';

CREATE TABLE IF NOT EXISTS ambassador_referral_links (
    id UUID PRIMARY KEY,
    ambassador_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    segment VARCHAR(255),
    city VARCHAR(255),
    code VARCHAR(255) NOT NULL,
    url VARCHAR(500) NOT NULL,
    clicks INT NOT NULL DEFAULT 0,
    conversions INT NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_ambassador_referral_links_code
    ON ambassador_referral_links (code);

CREATE INDEX IF NOT EXISTS idx_ambassador_referral_links_ambassador_id
    ON ambassador_referral_links (ambassador_id);
