-- Flyway Migration V2: Seed Default Actions
-- Description: Inserts default actions for bootstrap tenant SYSTEM

INSERT INTO iam_action (tenant_id, code, name, version)
SELECT 'SYSTEM', 'CREATE', 'Create', 0
WHERE NOT EXISTS (
    SELECT 1 FROM iam_action WHERE tenant_id = 'SYSTEM' AND code = 'CREATE'
);

INSERT INTO iam_action (tenant_id, code, name, version)
SELECT 'SYSTEM', 'READ', 'Read', 0
WHERE NOT EXISTS (
    SELECT 1 FROM iam_action WHERE tenant_id = 'SYSTEM' AND code = 'READ'
);

INSERT INTO iam_action (tenant_id, code, name, version)
SELECT 'SYSTEM', 'UPDATE', 'Update', 0
WHERE NOT EXISTS (
    SELECT 1 FROM iam_action WHERE tenant_id = 'SYSTEM' AND code = 'UPDATE'
);

INSERT INTO iam_action (tenant_id, code, name, version)
SELECT 'SYSTEM', 'DELETE', 'Delete', 0
WHERE NOT EXISTS (
    SELECT 1 FROM iam_action WHERE tenant_id = 'SYSTEM' AND code = 'DELETE'
);

INSERT INTO iam_action (tenant_id, code, name, version)
SELECT 'SYSTEM', 'EXECUTE', 'Execute', 0
WHERE NOT EXISTS (
    SELECT 1 FROM iam_action WHERE tenant_id = 'SYSTEM' AND code = 'EXECUTE'
);
