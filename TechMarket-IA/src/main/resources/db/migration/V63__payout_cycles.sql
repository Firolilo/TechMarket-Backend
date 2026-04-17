CREATE TABLE IF NOT EXISTS payout_cycles (
    id UUID PRIMARY KEY,
    start_date DATE,
    end_date DATE,
    status VARCHAR(255),
    total_amount NUMERIC(14,2),
    generated_at TIMESTAMPTZ
);
