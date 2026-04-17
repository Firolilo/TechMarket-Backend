CREATE TABLE IF NOT EXISTS service_appointments (
    id UUID PRIMARY KEY,
    ticket_id UUID,
    tenant_id UUID,
    assigned_technician_user_id UUID,
    start_at TIMESTAMPTZ,
    end_at TIMESTAMPTZ,
    location VARCHAR(255),
    status VARCHAR(255),
    notes VARCHAR(255)
);
