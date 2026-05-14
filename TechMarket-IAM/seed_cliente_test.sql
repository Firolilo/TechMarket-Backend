CREATE EXTENSION IF NOT EXISTS pgcrypto;

BEGIN;

-- Usuario IAM cliente de prueba
-- Email: cliente.test@techmarket.com / Password: Cliente123!
INSERT INTO iam_user (
    tenant_id,
    username,
    email,
    first_name,
    last_name,
    phone,
    country,
    city,
    user_type,
    terms_accepted,
    active,
    created_at,
    last_modified_at
)
SELECT
    '00000000-0000-0000-0000-000000000000',
    'cliente.test@techmarket.com',
    'cliente.test@techmarket.com',
    'Sofia',
    'Flores',
    '+59171234567',
    'Bolivia',
    'La Paz',
    'cliente',
    TRUE,
    TRUE,
    NOW(),
    NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM iam_user
    WHERE tenant_id = '00000000-0000-0000-0000-000000000000'
      AND username = 'cliente.test@techmarket.com'
);

-- Credencial con BCrypt
INSERT INTO iam_user_credential (user_id, tenant_id, password_hash, created_at, last_modified_at)
SELECT
    u.id,
    '00000000-0000-0000-0000-000000000000',
    crypt('Cliente123!', gen_salt('bf', 10)),
    NOW(),
    NOW()
FROM iam_user u
WHERE u.username = 'cliente.test@techmarket.com'
  AND u.tenant_id = '00000000-0000-0000-0000-000000000000'
  AND NOT EXISTS (
      SELECT 1 FROM iam_user_credential c
      WHERE c.user_id = u.id
  );

-- Scope GLOBAL
INSERT INTO iam_user_scope (tenant_id, user_id, branch_id, scope_type, created_at)
SELECT
    '00000000-0000-0000-0000-000000000000',
    u.id,
    NULL,
    'GLOBAL',
    NOW()
FROM iam_user u
WHERE u.username = 'cliente.test@techmarket.com'
  AND u.tenant_id = '00000000-0000-0000-0000-000000000000'
  AND NOT EXISTS (
      SELECT 1 FROM iam_user_scope s
      WHERE s.user_id = u.id AND s.scope_type = 'GLOBAL'
  );

-- Rol cliente
INSERT INTO iam_user_roles (user_id, role_id)
SELECT u.id, r.id
FROM iam_user u, iam_role r
WHERE u.username = 'cliente.test@techmarket.com'
  AND u.tenant_id = '00000000-0000-0000-0000-000000000000'
  AND r.name = 'cliente'
  AND r.tenant_id = '00000000-0000-0000-0000-000000000000'
  AND NOT EXISTS (
      SELECT 1 FROM iam_user_roles ur
      WHERE ur.user_id = u.id AND ur.role_id = r.id
  );

COMMIT;
