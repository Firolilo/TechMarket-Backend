ALTER TABLE ambassador_leads ADD COLUMN IF NOT EXISTS email VARCHAR(255);
ALTER TABLE ambassador_leads ADD COLUMN IF NOT EXISTS city VARCHAR(255);
ALTER TABLE ambassador_leads ADD COLUMN IF NOT EXISTS country VARCHAR(255);
ALTER TABLE ambassador_leads ADD COLUMN IF NOT EXISTS notes TEXT;
ALTER TABLE ambassador_leads ADD COLUMN IF NOT EXISTS next_action VARCHAR(1000);
ALTER TABLE ambassador_leads ADD COLUMN IF NOT EXISTS last_contact_at TIMESTAMPTZ;
ALTER TABLE ambassador_referrals ADD COLUMN IF NOT EXISTS country VARCHAR(255);

CREATE TABLE IF NOT EXISTS ambassador_lead_activities (
    id UUID PRIMARY KEY,
    lead_id UUID NOT NULL,
    activity_type VARCHAR(255) NOT NULL,
    note VARCHAR(1000),
    created_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_ambassador_lead_activities_lead_id
    ON ambassador_lead_activities (lead_id);
