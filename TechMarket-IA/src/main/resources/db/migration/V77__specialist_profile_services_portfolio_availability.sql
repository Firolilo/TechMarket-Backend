CREATE TABLE IF NOT EXISTS specialist_profiles (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    specialty VARCHAR(255),
    location VARCHAR(255),
    photo_url VARCHAR(255),
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ,
    CONSTRAINT uk_specialist_profiles_user_id UNIQUE (user_id)
);

CREATE TABLE IF NOT EXISTS specialist_services (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    price NUMERIC(14,2),
    currency VARCHAR(16),
    service_type VARCHAR(255),
    featured BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS specialist_portfolio_items (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    service_name VARCHAR(255),
    result VARCHAR(255),
    work_date VARCHAR(32),
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS specialist_availability (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    status VARCHAR(32) NOT NULL,
    days_json TEXT,
    start_time VARCHAR(16),
    end_time VARCHAR(16),
    modalities_json TEXT,
    coverage VARCHAR(255),
    response_time VARCHAR(255),
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ,
    CONSTRAINT uk_specialist_availability_user_id UNIQUE (user_id)
);

CREATE INDEX IF NOT EXISTS idx_specialist_services_user_id ON specialist_services (user_id);
CREATE INDEX IF NOT EXISTS idx_specialist_portfolio_items_user_id
    ON specialist_portfolio_items (user_id);
CREATE INDEX IF NOT EXISTS idx_service_appointments_technician_status
    ON service_appointments (assigned_technician_user_id, status);
CREATE INDEX IF NOT EXISTS idx_reviews_ticket_id ON reviews (ticket_id);
