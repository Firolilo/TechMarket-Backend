CREATE TABLE IF NOT EXISTS tenant_verification_documents (
    id UUID PRIMARY KEY,
    tenant_verification_id UUID,
    document_type VARCHAR(255),
    file_url VARCHAR(255),
    status VARCHAR(255),
    uploaded_by_user_id UUID,
    uploaded_at TIMESTAMPTZ
);
