-- Flyway Migration V5: Public user registration profile fields.

ALTER TABLE iam_user
    ADD COLUMN IF NOT EXISTS first_name VARCHAR(100);

ALTER TABLE iam_user
    ADD COLUMN IF NOT EXISTS last_name VARCHAR(100);

ALTER TABLE iam_user
    ADD COLUMN IF NOT EXISTS phone VARCHAR(30);

ALTER TABLE iam_user
    ADD COLUMN IF NOT EXISTS country VARCHAR(100);

ALTER TABLE iam_user
    ADD COLUMN IF NOT EXISTS city VARCHAR(100);

ALTER TABLE iam_user
    ADD COLUMN IF NOT EXISTS user_type VARCHAR(50);

ALTER TABLE iam_user
    ADD COLUMN IF NOT EXISTS terms_accepted BOOLEAN NOT NULL DEFAULT FALSE;