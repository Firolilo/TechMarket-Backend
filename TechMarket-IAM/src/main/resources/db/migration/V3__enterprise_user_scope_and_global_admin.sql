-- Flyway Migration V3: Enterprise user scopes and role hierarchy

ALTER TABLE iam_role
    ADD COLUMN IF NOT EXISTS hierarchy_level INTEGER NOT NULL DEFAULT 100;

ALTER TABLE iam_role
    ADD COLUMN IF NOT EXISTS parent_role_id BIGINT;

ALTER TABLE iam_role
    ADD CONSTRAINT fk_iam_role_parent_role
        FOREIGN KEY (parent_role_id) REFERENCES iam_role (id);

CREATE INDEX IF NOT EXISTS idx_iam_role_parent_role ON iam_role(parent_role_id);
CREATE INDEX IF NOT EXISTS idx_iam_role_tenant_hierarchy ON iam_role(tenant_id, hierarchy_level);

CREATE TABLE IF NOT EXISTS iam_branch (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(100) NOT NULL,
    code VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_at TIMESTAMP,
    CONSTRAINT uk_iam_branch_tenant_code UNIQUE (tenant_id, code)
);

CREATE TABLE IF NOT EXISTS iam_user_scope (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(100) NOT NULL,
    user_id BIGINT NOT NULL REFERENCES iam_user(id) ON DELETE CASCADE,
    branch_id BIGINT REFERENCES iam_branch(id),
    scope_type VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_iam_branch_tenant ON iam_branch(tenant_id);
CREATE INDEX IF NOT EXISTS idx_iam_user_scope_user ON iam_user_scope(user_id);
CREATE INDEX IF NOT EXISTS idx_iam_user_scope_branch ON iam_user_scope(branch_id);
CREATE INDEX IF NOT EXISTS idx_iam_user_scope_tenant ON iam_user_scope(tenant_id);

-- Align audit_log with core-platform audit model while keeping previous columns for compatibility.
ALTER TABLE audit_log
    ADD COLUMN IF NOT EXISTS action VARCHAR(100);

ALTER TABLE audit_log
    ADD COLUMN IF NOT EXISTS entity VARCHAR(255);

ALTER TABLE audit_log
    ADD COLUMN IF NOT EXISTS occurred_at TIMESTAMP;

ALTER TABLE audit_log
    ALTER COLUMN event_type DROP NOT NULL;

UPDATE audit_log
SET action = COALESCE(action, event_type),
    entity = COALESCE(entity, entity_type),
    occurred_at = COALESCE(occurred_at, CURRENT_TIMESTAMP);

CREATE INDEX IF NOT EXISTS idx_audit_log_occurred_at ON audit_log(occurred_at DESC);
