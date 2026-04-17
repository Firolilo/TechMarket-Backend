CREATE TABLE IF NOT EXISTS tickets (
    id UUID PRIMARY KEY,
    ticket_code VARCHAR(255),
    tenant_id UUID,
    branch_id UUID,
    customer_user_id UUID,
    lead_id UUID,
    listing_id UUID,
    assigned_user_id UUID,
    assigned_technician_user_id UUID,
    ticket_type VARCHAR(255),
    subject VARCHAR(255),
    description VARCHAR(255),
    priority VARCHAR(255),
    status VARCHAR(255),
    opened_at TIMESTAMPTZ,
    closed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ
);
