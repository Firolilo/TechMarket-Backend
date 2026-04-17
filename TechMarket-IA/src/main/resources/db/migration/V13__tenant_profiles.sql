CREATE TABLE IF NOT EXISTS tenant_profiles (
    id UUID PRIMARY KEY,
    tenant_id UUID,
    logo_url VARCHAR(255),
    cover_image_url VARCHAR(255),
    short_description VARCHAR(255),
    full_description VARCHAR(255),
    website_url VARCHAR(255),
    facebook_url VARCHAR(255),
    instagram_url VARCHAR(255),
    rating_average NUMERIC(3,2),
    reviews_count INT
);
