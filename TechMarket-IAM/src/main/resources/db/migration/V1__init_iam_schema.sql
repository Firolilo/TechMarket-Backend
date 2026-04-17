-- Flyway Migration V1: Initial IAM schema for iam-service
-- Description: Core IAM tables + auth credential and refresh token tables

CREATE TABLE IF NOT EXISTS iam_module (
    id BIGSERIAL PRIMARY KEY,
    version BIGINT NOT NULL DEFAULT 0,
    tenant_id VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    last_modified_at TIMESTAMP,
    last_modified_by VARCHAR(100),
    code VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    CONSTRAINT uk_iam_module_tenant_code UNIQUE (tenant_id, code),
    CONSTRAINT uk_iam_module_tenant_name UNIQUE (tenant_id, name)
);

CREATE TABLE IF NOT EXISTS iam_resource (
    id BIGSERIAL PRIMARY KEY,
    version BIGINT NOT NULL DEFAULT 0,
    tenant_id VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    last_modified_at TIMESTAMP,
    last_modified_by VARCHAR(100),
    code VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    module_id BIGINT NOT NULL REFERENCES iam_module(id),
    CONSTRAINT uk_iam_resource_tenant_code UNIQUE (tenant_id, code)
);

CREATE TABLE IF NOT EXISTS iam_action (
    id BIGSERIAL PRIMARY KEY,
    version BIGINT NOT NULL DEFAULT 0,
    tenant_id VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    last_modified_at TIMESTAMP,
    last_modified_by VARCHAR(100),
    code VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    CONSTRAINT uk_iam_action_tenant_code UNIQUE (tenant_id, code)
);

CREATE TABLE IF NOT EXISTS iam_field (
    id BIGSERIAL PRIMARY KEY,
    version BIGINT NOT NULL DEFAULT 0,
    tenant_id VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    last_modified_at TIMESTAMP,
    last_modified_by VARCHAR(100),
    name VARCHAR(100) NOT NULL,
    resource_id BIGINT NOT NULL REFERENCES iam_resource(id)
);

CREATE TABLE IF NOT EXISTS iam_role (
    id BIGSERIAL PRIMARY KEY,
    version BIGINT NOT NULL DEFAULT 0,
    tenant_id VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    last_modified_at TIMESTAMP,
    last_modified_by VARCHAR(100),
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    CONSTRAINT uk_iam_role_tenant_name UNIQUE (tenant_id, name)
);

CREATE TABLE IF NOT EXISTS iam_user (
    id BIGSERIAL PRIMARY KEY,
    version BIGINT NOT NULL DEFAULT 0,
    tenant_id VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    last_modified_at TIMESTAMP,
    last_modified_by VARCHAR(100),
    username VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uk_iam_user_tenant_username UNIQUE (tenant_id, username),
    CONSTRAINT uk_iam_user_tenant_email UNIQUE (tenant_id, email)
);

CREATE TABLE IF NOT EXISTS iam_permission (
    id BIGSERIAL PRIMARY KEY,
    version BIGINT NOT NULL DEFAULT 0,
    tenant_id VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    last_modified_at TIMESTAMP,
    last_modified_by VARCHAR(100),
    role_id BIGINT NOT NULL REFERENCES iam_role(id) ON DELETE CASCADE,
    module_id BIGINT NOT NULL REFERENCES iam_module(id),
    resource_id BIGINT NOT NULL REFERENCES iam_resource(id),
    action_id BIGINT NOT NULL REFERENCES iam_action(id),
    field_id BIGINT REFERENCES iam_field(id)
);

CREATE TABLE IF NOT EXISTS iam_user_roles (
    user_id BIGINT NOT NULL REFERENCES iam_user(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES iam_role(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE IF NOT EXISTS iam_user_credential (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES iam_user(id) ON DELETE CASCADE,
    tenant_id VARCHAR(100) NOT NULL,
    password_hash VARCHAR(120) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_at TIMESTAMP,
    CONSTRAINT uk_iam_user_credential_user UNIQUE (user_id),
    CONSTRAINT uk_iam_user_credential_tenant_user UNIQUE (tenant_id, user_id)
);

CREATE TABLE IF NOT EXISTS iam_refresh_token (
    id BIGSERIAL PRIMARY KEY,
    token_id VARCHAR(120) NOT NULL,
    user_id BIGINT NOT NULL REFERENCES iam_user(id) ON DELETE CASCADE,
    tenant_id VARCHAR(100) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_iam_refresh_token_token_id UNIQUE (token_id)
);

CREATE TABLE IF NOT EXISTS audit_log (
    id BIGSERIAL PRIMARY KEY,
    version BIGINT NOT NULL DEFAULT 0,
    tenant_id VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    last_modified_at TIMESTAMP,
    last_modified_by VARCHAR(100),
    event_type VARCHAR(100) NOT NULL,
    entity_type VARCHAR(255),
    entity_id VARCHAR(255),
    user_id VARCHAR(100),
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    details TEXT,
    correlation_id VARCHAR(100)
);

CREATE INDEX IF NOT EXISTS idx_iam_module_tenant ON iam_module(tenant_id);
CREATE INDEX IF NOT EXISTS idx_iam_resource_tenant ON iam_resource(tenant_id);
CREATE INDEX IF NOT EXISTS idx_iam_action_tenant ON iam_action(tenant_id);
CREATE INDEX IF NOT EXISTS idx_iam_field_tenant ON iam_field(tenant_id);
CREATE INDEX IF NOT EXISTS idx_iam_role_tenant ON iam_role(tenant_id);
CREATE INDEX IF NOT EXISTS idx_iam_user_tenant ON iam_user(tenant_id);
CREATE INDEX IF NOT EXISTS idx_iam_permission_tenant ON iam_permission(tenant_id);
CREATE INDEX IF NOT EXISTS idx_iam_user_roles_user ON iam_user_roles(user_id);
CREATE INDEX IF NOT EXISTS idx_iam_user_roles_role ON iam_user_roles(role_id);
CREATE INDEX IF NOT EXISTS idx_iam_user_credential_tenant ON iam_user_credential(tenant_id);
CREATE INDEX IF NOT EXISTS idx_iam_refresh_token_tenant ON iam_refresh_token(tenant_id);
CREATE INDEX IF NOT EXISTS idx_iam_refresh_token_user ON iam_refresh_token(user_id);
CREATE INDEX IF NOT EXISTS idx_audit_log_event_type ON audit_log(event_type);
CREATE INDEX IF NOT EXISTS idx_audit_log_tenant ON audit_log(tenant_id);
CREATE INDEX IF NOT EXISTS idx_audit_log_timestamp ON audit_log(timestamp DESC);
