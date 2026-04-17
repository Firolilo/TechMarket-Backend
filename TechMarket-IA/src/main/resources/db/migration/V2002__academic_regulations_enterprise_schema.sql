ALTER TABLE academic_regulation
    ADD COLUMN updated_at TIMESTAMP WITH TIME ZONE,
    ADD COLUMN inactivated_at TIMESTAMP WITH TIME ZONE,
    ADD COLUMN inactivated_by VARCHAR(100),
    ADD COLUMN inactivation_reason TEXT,
    ADD COLUMN record_version INT NOT NULL DEFAULT 0;

ALTER TABLE academic_regulation_version
    ADD COLUMN submitted_by VARCHAR(100),
    ADD COLUMN approved_by VARCHAR(100),
    ADD COLUMN rejected_at TIMESTAMP WITH TIME ZONE,
    ADD COLUMN rejected_by VARCHAR(100),
    ADD COLUMN rejection_reason TEXT,
    ADD COLUMN published_by VARCHAR(100),
    ADD COLUMN deprecated_at TIMESTAMP WITH TIME ZONE,
    ADD COLUMN deprecated_by VARCHAR(100),
    ADD COLUMN deprecation_reason TEXT,
    ADD COLUMN version_notes TEXT,
    ADD COLUMN change_summary_i18n JSONB,
    ADD COLUMN record_version INT NOT NULL DEFAULT 0;

ALTER TABLE academic_regulation_version
    ADD CONSTRAINT chk_academic_regulation_version_effective_range
        CHECK (effective_to IS NULL OR effective_from IS NULL OR effective_to >= effective_from);

ALTER TABLE academic_regulation_publication_log
    ADD COLUMN correlation_id VARCHAR(100),
    ADD COLUMN action_reason TEXT,
    ADD COLUMN reference_note TEXT,
    ADD COLUMN record_version INT NOT NULL DEFAULT 0;

CREATE TABLE academic_regulation_version_evidence_relation (
    id UUID PRIMARY KEY,
    regulation_version_id UUID NOT NULL REFERENCES academic_regulation_version (id) ON DELETE CASCADE,
    digital_asset_id UUID NOT NULL,
    evidence_relation_type VARCHAR(100) NOT NULL,
    display_name VARCHAR(255),
    description_i18n JSONB,
    metadata_json JSONB,
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    associated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    associated_by VARCHAR(100),
    replaced_at TIMESTAMP WITH TIME ZONE,
    replaced_by VARCHAR(100),
    replacement_reason TEXT,
    record_version INT NOT NULL DEFAULT 0
);

CREATE TABLE academic_regulation_parameter_link (
    id UUID PRIMARY KEY,
    regulation_version_id UUID NOT NULL REFERENCES academic_regulation_version (id) ON DELETE CASCADE,
    parameter_code VARCHAR(100) NOT NULL,
    link_type VARCHAR(50) NOT NULL,
    link_reason TEXT,
    linked_at TIMESTAMP WITH TIME ZONE NOT NULL,
    linked_by VARCHAR(100),
    unlinked_at TIMESTAMP WITH TIME ZONE,
    unlinked_by VARCHAR(100),
    unlink_reason TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    record_version INT NOT NULL DEFAULT 0
);

CREATE TABLE academic_regulation_audit_snapshot (
    id UUID PRIMARY KEY,
    aggregate_id UUID NOT NULL,
    aggregate_type VARCHAR(100) NOT NULL,
    snapshot_json JSONB NOT NULL,
    captured_at TIMESTAMP WITH TIME ZONE NOT NULL,
    captured_by VARCHAR(100),
    capture_reason TEXT,
    correlation_id VARCHAR(100),
    record_version INT NOT NULL DEFAULT 0
);

CREATE TABLE academic_regulation_conflict (
    id UUID PRIMARY KEY,
    regulation_code VARCHAR(100) NOT NULL,
    left_regulation_version_id UUID NOT NULL REFERENCES academic_regulation_version (id) ON DELETE CASCADE,
    right_regulation_version_id UUID NOT NULL REFERENCES academic_regulation_version (id) ON DELETE CASCADE,
    conflict_type VARCHAR(100) NOT NULL,
    conflict_status VARCHAR(50) NOT NULL,
    conflict_scope JSONB,
    detection_reason TEXT,
    detected_at TIMESTAMP WITH TIME ZONE NOT NULL,
    detected_by VARCHAR(100),
    resolution_strategy VARCHAR(100),
    resolved_at TIMESTAMP WITH TIME ZONE,
    resolved_by VARCHAR(100),
    resolution_notes TEXT,
    record_version INT NOT NULL DEFAULT 0,
    CONSTRAINT chk_academic_regulation_conflict_distinct_versions
        CHECK (left_regulation_version_id <> right_regulation_version_id)
);

CREATE INDEX idx_academic_regulation_active
    ON academic_regulation (active);

CREATE INDEX idx_academic_regulation_inactivated_at
    ON academic_regulation (inactivated_at);

CREATE INDEX idx_academic_regulation_version_effective_period
    ON academic_regulation_version (effective_from, effective_to);

CREATE INDEX idx_academic_regulation_version_publication_lookup
    ON academic_regulation_version (
        regulation_id,
        publication_status,
        approval_status,
        effective_from,
        effective_to
    );

CREATE INDEX idx_academic_regulation_version_deprecated_at
    ON academic_regulation_version (deprecated_at);

CREATE INDEX idx_academic_regulation_publication_log_version_occurred
    ON academic_regulation_publication_log (regulation_version_id, occurred_at DESC);

CREATE INDEX idx_academic_regulation_publication_log_correlation
    ON academic_regulation_publication_log (correlation_id);

CREATE INDEX idx_academic_regulation_evidence_version
    ON academic_regulation_version_evidence_relation (regulation_version_id);

CREATE INDEX idx_academic_regulation_evidence_asset
    ON academic_regulation_version_evidence_relation (digital_asset_id);

CREATE INDEX idx_academic_regulation_evidence_type
    ON academic_regulation_version_evidence_relation (evidence_relation_type);

CREATE UNIQUE INDEX uq_academic_regulation_evidence_active_primary
    ON academic_regulation_version_evidence_relation (regulation_version_id)
    WHERE is_primary = TRUE AND active = TRUE;

CREATE INDEX idx_academic_regulation_parameter_link_version
    ON academic_regulation_parameter_link (regulation_version_id);

CREATE INDEX idx_academic_regulation_parameter_link_parameter
    ON academic_regulation_parameter_link (parameter_code);

CREATE INDEX idx_academic_regulation_parameter_link_active
    ON academic_regulation_parameter_link (active, parameter_code);

CREATE UNIQUE INDEX uq_academic_regulation_parameter_link_active
    ON academic_regulation_parameter_link (regulation_version_id, parameter_code, link_type)
    WHERE active = TRUE;

CREATE INDEX idx_academic_regulation_audit_snapshot_aggregate
    ON academic_regulation_audit_snapshot (aggregate_type, aggregate_id);

CREATE INDEX idx_academic_regulation_audit_snapshot_captured_at
    ON academic_regulation_audit_snapshot (captured_at DESC);

CREATE INDEX idx_academic_regulation_audit_snapshot_correlation
    ON academic_regulation_audit_snapshot (correlation_id);

CREATE INDEX idx_academic_regulation_conflict_code
    ON academic_regulation_conflict (regulation_code);

CREATE INDEX idx_academic_regulation_conflict_status
    ON academic_regulation_conflict (conflict_status);

CREATE INDEX idx_academic_regulation_conflict_versions
    ON academic_regulation_conflict (left_regulation_version_id, right_regulation_version_id);

CREATE INDEX idx_academic_regulation_conflict_detected_at
    ON academic_regulation_conflict (detected_at DESC);
