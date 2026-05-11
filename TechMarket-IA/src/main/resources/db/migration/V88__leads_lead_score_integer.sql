ALTER TABLE leads
    ALTER COLUMN lead_score TYPE INTEGER
    USING NULLIF(TRIM(lead_score::text), '')::INTEGER;