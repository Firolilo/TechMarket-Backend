CREATE TABLE IF NOT EXISTS payout_items (
    id UUID PRIMARY KEY,
    payout_cycle_id UUID,
    ambassador_id UUID,
    ambassador_commission_id UUID,
    amount VARCHAR(255),
    status VARCHAR(255)
);
