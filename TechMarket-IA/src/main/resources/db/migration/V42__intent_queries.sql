CREATE TABLE IF NOT EXISTS intent_queries (
    id UUID PRIMARY KEY,
    user_id UUID,
    original_query VARCHAR(255),
    detected_intent VARCHAR(255),
    extracted_entities_json JSONB,
    response_payload_json JSONB,
    created_at TIMESTAMPTZ
);
