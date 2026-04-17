CREATE TABLE academic_regulation (
    id UUID PRIMARY KEY,
    code VARCHAR(100) NOT NULL UNIQUE,
    regulation_type VARCHAR(50) NOT NULL,
    issuing_authority VARCHAR(150) NOT NULL,
    title_i18n JSONB NOT NULL,
    description_i18n JSONB NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE academic_regulation_version (
    id UUID PRIMARY KEY,
    regulation_id UUID NOT NULL REFERENCES academic_regulation (id),
    version_number INT NOT NULL,
    version_title_i18n JSONB NOT NULL,
    summary_i18n JSONB NOT NULL,
    effective_from DATE,
    effective_to DATE,
    approval_status VARCHAR(30) NOT NULL,
    publication_status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    submitted_at TIMESTAMP WITH TIME ZONE,
    approved_at TIMESTAMP WITH TIME ZONE,
    published_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT uq_academic_regulation_version UNIQUE (regulation_id, version_number)
);

CREATE TABLE academic_regulation_scope (
    regulation_version_id UUID PRIMARY KEY REFERENCES academic_regulation_version (id) ON DELETE CASCADE,
    site_code VARCHAR(50),
    academic_level_code VARCHAR(50),
    modality_code VARCHAR(50),
    general_scope BOOLEAN NOT NULL
);

CREATE TABLE academic_regulation_publication_log (
    id UUID PRIMARY KEY,
    regulation_version_id UUID NOT NULL REFERENCES academic_regulation_version (id),
    action VARCHAR(100) NOT NULL,
    actor VARCHAR(100) NOT NULL,
    occurred_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_academic_regulation_code ON academic_regulation (code);
CREATE INDEX idx_academic_regulation_version_regulation_id
    ON academic_regulation_version (regulation_id, version_number);
CREATE INDEX idx_academic_regulation_version_status
    ON academic_regulation_version (approval_status, publication_status);
CREATE INDEX idx_academic_regulation_scope_lookup
    ON academic_regulation_scope (site_code, academic_level_code, modality_code, general_scope);
