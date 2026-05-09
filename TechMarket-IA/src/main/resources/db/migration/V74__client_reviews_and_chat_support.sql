ALTER TABLE reviews ADD COLUMN IF NOT EXISTS listing_id UUID;
ALTER TABLE reviews ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ;

CREATE INDEX IF NOT EXISTS idx_reviews_listing_id ON reviews (listing_id);
CREATE INDEX IF NOT EXISTS idx_reviews_user_id ON reviews (user_id);
CREATE INDEX IF NOT EXISTS idx_tickets_customer_chat
    ON tickets (customer_user_id, ticket_type, created_at);
CREATE INDEX IF NOT EXISTS idx_ticket_messages_ticket_created
    ON ticket_messages (ticket_id, created_at);
