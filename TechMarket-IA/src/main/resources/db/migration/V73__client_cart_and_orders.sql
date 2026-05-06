CREATE TABLE IF NOT EXISTS client_cart_items (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    listing_id UUID NOT NULL,
    quantity INT NOT NULL,
    unit_price NUMERIC(14,2),
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS client_orders (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    shipping_address_id UUID,
    payment_method VARCHAR(255),
    status VARCHAR(255),
    total NUMERIC(14,2),
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS client_order_items (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    listing_id UUID NOT NULL,
    quantity INT NOT NULL,
    unit_price NUMERIC(14,2)
);

CREATE INDEX IF NOT EXISTS idx_client_cart_items_user_id ON client_cart_items (user_id);
CREATE INDEX IF NOT EXISTS idx_client_orders_user_id ON client_orders (user_id);
CREATE INDEX IF NOT EXISTS idx_client_order_items_order_id ON client_order_items (order_id);
