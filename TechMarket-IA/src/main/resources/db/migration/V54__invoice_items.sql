CREATE TABLE IF NOT EXISTS invoice_items (
    id UUID PRIMARY KEY,
    invoice_id UUID,
    description VARCHAR(255),
    quantity INT,
    unit_price NUMERIC(14,2),
    line_total NUMERIC(14,2)
);
