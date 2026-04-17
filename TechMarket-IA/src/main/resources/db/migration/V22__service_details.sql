CREATE TABLE IF NOT EXISTS service_details (
    id UUID PRIMARY KEY,
    listing_id UUID,
    estimated_duration_minutes INT,
    requires_diagnosis VARCHAR(255),
    offers_on_site_service VARCHAR(255),
    service_area VARCHAR(255),
    terms_and_conditions VARCHAR(255)
);
