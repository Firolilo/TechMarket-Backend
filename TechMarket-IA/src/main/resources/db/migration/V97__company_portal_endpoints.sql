ALTER TABLE tenants ADD COLUMN IF NOT EXISTS business_type VARCHAR(255);
ALTER TABLE tenants ADD COLUMN IF NOT EXISTS description VARCHAR(255);

ALTER TABLE tenant_profiles ADD COLUMN IF NOT EXISTS logo_text VARCHAR(32);
ALTER TABLE tenant_profiles ADD COLUMN IF NOT EXISTS slogan VARCHAR(255);
ALTER TABLE tenant_profiles ADD COLUMN IF NOT EXISTS specialization VARCHAR(255);
ALTER TABLE tenant_profiles ADD COLUMN IF NOT EXISTS category VARCHAR(255);
ALTER TABLE tenant_profiles ADD COLUMN IF NOT EXISTS experience_years INT;
ALTER TABLE tenant_profiles ADD COLUMN IF NOT EXISTS about VARCHAR(1000);
ALTER TABLE tenant_profiles ADD COLUMN IF NOT EXISTS addresses_json TEXT;
ALTER TABLE tenant_profiles ADD COLUMN IF NOT EXISTS coverage_areas_json TEXT;
ALTER TABLE tenant_profiles ADD COLUMN IF NOT EXISTS contacts_json TEXT;
ALTER TABLE tenant_profiles ADD COLUMN IF NOT EXISTS social_links_json TEXT;
ALTER TABLE tenant_profiles ADD COLUMN IF NOT EXISTS schedules_json TEXT;
ALTER TABLE tenant_profiles ADD COLUMN IF NOT EXISTS branches_json TEXT;
ALTER TABLE tenant_profiles ADD COLUMN IF NOT EXISTS location_overview_json TEXT;
ALTER TABLE tenant_profiles ADD COLUMN IF NOT EXISTS settings_json TEXT;

ALTER TABLE listings ADD COLUMN IF NOT EXISTS image_url VARCHAR(255);
ALTER TABLE listings ADD COLUMN IF NOT EXISTS previous_price NUMERIC(14,2);
ALTER TABLE listings ADD COLUMN IF NOT EXISTS label VARCHAR(255);

ALTER TABLE feed_posts ADD COLUMN IF NOT EXISTS image_url VARCHAR(255);
ALTER TABLE feed_posts ADD COLUMN IF NOT EXISTS price NUMERIC(14,2);
ALTER TABLE feed_posts ADD COLUMN IF NOT EXISTS previous_price NUMERIC(14,2);
ALTER TABLE feed_posts ADD COLUMN IF NOT EXISTS label VARCHAR(255);
ALTER TABLE feed_posts ADD COLUMN IF NOT EXISTS options_json TEXT;
ALTER TABLE feed_posts ADD COLUMN IF NOT EXISTS company_listing_id UUID;

CREATE TABLE IF NOT EXISTS company_publication_likes (
    id UUID PRIMARY KEY,
    feed_post_id UUID NOT NULL,
    user_id UUID,
    tenant_id UUID NOT NULL,
    liked BOOLEAN NOT NULL,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_company_publication_likes_post
    ON company_publication_likes (feed_post_id);

CREATE INDEX IF NOT EXISTS idx_company_publication_likes_user_post
    ON company_publication_likes (user_id, feed_post_id);
