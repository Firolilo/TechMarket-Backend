CREATE TABLE IF NOT EXISTS user_notification_preferences (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    email_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    push_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    in_app_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_user_notification_preferences_user_id
    ON user_notification_preferences (user_id);

CREATE TABLE IF NOT EXISTS user_payment_methods (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    method_type VARCHAR(255) NOT NULL,
    brand VARCHAR(255),
    last4 VARCHAR(4),
    token_reference VARCHAR(255),
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_user_payment_methods_user_id
    ON user_payment_methods (user_id);

CREATE TABLE IF NOT EXISTS user_payment_intents (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    reference_id VARCHAR(255) NOT NULL,
    amount NUMERIC(14,2) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    payment_method_id UUID,
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ,
    confirmed_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_user_payment_intents_user_id
    ON user_payment_intents (user_id);

CREATE TABLE IF NOT EXISTS user_transactions (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    payment_intent_id UUID,
    concept VARCHAR(255) NOT NULL,
    amount NUMERIC(14,2) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_user_transactions_user_id
    ON user_transactions (user_id);

CREATE INDEX IF NOT EXISTS idx_user_transactions_created_at
    ON user_transactions (created_at);
