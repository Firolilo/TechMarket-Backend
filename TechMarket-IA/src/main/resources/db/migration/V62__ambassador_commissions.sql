CREATE TABLE IF NOT EXISTS ambassador_commissions (
    id UUID PRIMARY KEY,
    ambassador_id UUID,
    ambassador_referral_id UUID,
    commission_rule_id UUID,
    attribution_type VARCHAR(255),
    event_type VARCHAR(255),
    reference_type VARCHAR(255),
    reference_id UUID,
    amount VARCHAR(255),
    status VARCHAR(255),
    generated_at TIMESTAMPTZ
);
