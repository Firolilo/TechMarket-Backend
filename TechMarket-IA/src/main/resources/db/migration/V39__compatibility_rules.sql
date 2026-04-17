CREATE TABLE IF NOT EXISTS compatibility_rules (
    id UUID PRIMARY KEY,
    source_component_type_id UUID,
    source_attribute VARCHAR(255),
    operator VARCHAR(255),
    target_component_type_id UUID,
    target_attribute VARCHAR(255),
    severity VARCHAR(255),
    explanation_message VARCHAR(255),
    is_active BOOLEAN
);
