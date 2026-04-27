-- Flyway Migration V5: Public registration and OTP challenge fields.

ALTER TABLE iam_user_credential
    ADD COLUMN IF NOT EXISTS otp_enabled BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE iam_user_credential
    ADD COLUMN IF NOT EXISTS otp_code_hash VARCHAR(255);

ALTER TABLE iam_user_credential
    ADD COLUMN IF NOT EXISTS otp_challenge_id VARCHAR(120);

ALTER TABLE iam_user_credential
    ADD COLUMN IF NOT EXISTS otp_expires_at TIMESTAMP;

ALTER TABLE iam_user_credential
    ADD COLUMN IF NOT EXISTS otp_attempts INTEGER NOT NULL DEFAULT 0;

CREATE INDEX IF NOT EXISTS idx_iam_user_credential_otp_challenge
    ON iam_user_credential(tenant_id, otp_challenge_id);
