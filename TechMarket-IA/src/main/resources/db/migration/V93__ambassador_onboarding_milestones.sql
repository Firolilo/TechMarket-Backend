CREATE TABLE IF NOT EXISTS ambassador_onboarding_milestones (
    id UUID PRIMARY KEY,
    ambassador_referral_id UUID NOT NULL,
    milestone_code VARCHAR(255) NOT NULL,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    completed_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_ambassador_onboarding_milestones_referral_id
    ON ambassador_onboarding_milestones (ambassador_referral_id);
