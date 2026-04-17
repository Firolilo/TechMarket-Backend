CREATE TABLE IF NOT EXISTS quote_items (
    id UUID PRIMARY KEY,
    quote_id UUID,
    listing_id UUID,
    description VARCHAR(255),
    quantity INT,
    unit_price NUMERIC(14,2),
    line_total NUMERIC(14,2)
);
