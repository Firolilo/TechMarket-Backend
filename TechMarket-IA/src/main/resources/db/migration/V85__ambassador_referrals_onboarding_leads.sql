ALTER TABLE ambassador_referrals ADD COLUMN IF NOT EXISTS name VARCHAR(255);
ALTER TABLE ambassador_referrals ADD COLUMN IF NOT EXISTS referral_type VARCHAR(255);
ALTER TABLE ambassador_referrals ADD COLUMN IF NOT EXISTS contact_name VARCHAR(255);
ALTER TABLE ambassador_referrals ADD COLUMN IF NOT EXISTS phone VARCHAR(50);
ALTER TABLE ambassador_referrals ADD COLUMN IF NOT EXISTS email VARCHAR(255);
ALTER TABLE ambassador_referrals ADD COLUMN IF NOT EXISTS city VARCHAR(255);
ALTER TABLE ambassador_referrals ADD COLUMN IF NOT EXISTS last_activity_at TIMESTAMPTZ;

CREATE TABLE IF NOT EXISTS ambassador_referral_activity (
    id UUID PRIMARY KEY,
    ambassador_referral_id UUID NOT NULL,
    activity_type VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    created_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_ambassador_referral_activity_referral_id
    ON ambassador_referral_activity (ambassador_referral_id);

CREATE TABLE IF NOT EXISTS ambassador_referral_notes (
    id UUID PRIMARY KEY,
    ambassador_referral_id UUID NOT NULL,
    note TEXT NOT NULL,
    created_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_ambassador_referral_notes_referral_id
    ON ambassador_referral_notes (ambassador_referral_id);

CREATE TABLE IF NOT EXISTS ambassador_referral_files (
    id UUID PRIMARY KEY,
    ambassador_referral_id UUID NOT NULL,
    url VARCHAR(500) NOT NULL,
    created_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_ambassador_referral_files_referral_id
    ON ambassador_referral_files (ambassador_referral_id);

CREATE TABLE IF NOT EXISTS ambassador_onboarding_tasks (
    id UUID PRIMARY KEY,
    ambassador_referral_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    due_date DATE,
    created_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_ambassador_onboarding_tasks_referral_id
    ON ambassador_onboarding_tasks (ambassador_referral_id);

CREATE TABLE IF NOT EXISTS ambassador_onboarding_reminders (
    id UUID PRIMARY KEY,
    ambassador_referral_id UUID NOT NULL,
    reminder_at TIMESTAMPTZ NOT NULL,
    message VARCHAR(1000),
    created_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_ambassador_onboarding_reminders_referral_id
    ON ambassador_onboarding_reminders (ambassador_referral_id);

CREATE TABLE IF NOT EXISTS ambassador_onboarding_milestones (
    id UUID PRIMARY KEY,
    ambassador_referral_id UUID NOT NULL,
    milestone_code VARCHAR(255) NOT NULL,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    completed_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_ambassador_onboarding_milestones_referral_id
    ON ambassador_onboarding_milestones (ambassador_referral_id);

CREATE TABLE IF NOT EXISTS ambassador_leads (
    id UUID PRIMARY KEY,
    ambassador_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    lead_type VARCHAR(255),
    contact_name VARCHAR(255),
    phone VARCHAR(50),
    source VARCHAR(255),
    status VARCHAR(255) NOT NULL,
    close_probability INT NOT NULL DEFAULT 50,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_ambassador_leads_ambassador_id
    ON ambassador_leads (ambassador_id);
