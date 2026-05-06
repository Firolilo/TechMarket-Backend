CREATE TABLE IF NOT EXISTS communities (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    members_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS community_memberships (
    id UUID PRIMARY KEY,
    community_id UUID NOT NULL,
    user_id UUID NOT NULL,
    joined_at TIMESTAMPTZ,
    CONSTRAINT uk_community_memberships_user_community UNIQUE (community_id, user_id)
);

CREATE TABLE IF NOT EXISTS chat_read_receipts (
    id UUID PRIMARY KEY,
    ticket_id UUID NOT NULL,
    user_id UUID NOT NULL,
    read_at TIMESTAMPTZ,
    CONSTRAINT uk_chat_read_receipts_ticket_user UNIQUE (ticket_id, user_id)
);

CREATE INDEX IF NOT EXISTS idx_favorites_user_listing ON favorites (user_id, listing_id);
CREATE INDEX IF NOT EXISTS idx_favorites_user_tenant ON favorites (user_id, tenant_id);
CREATE INDEX IF NOT EXISTS idx_community_memberships_user_id ON community_memberships (user_id);
CREATE INDEX IF NOT EXISTS idx_community_memberships_community_id
    ON community_memberships (community_id);
CREATE INDEX IF NOT EXISTS idx_chat_read_receipts_ticket_user
    ON chat_read_receipts (ticket_id, user_id);
