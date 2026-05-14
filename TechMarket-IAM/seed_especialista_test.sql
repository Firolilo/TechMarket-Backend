-- =============================================================================
-- SEEDER DE PRUEBA: Especialista en TechMarket-IAM
-- Base de datos: TechMarket-IAM (microservicio de autenticación)
-- Contraseña del usuario: Especialista123!
--
-- REQUISITO: la extensión pgcrypto debe estar habilitada.
-- Si no está, ejecútala una vez como superusuario:
--   CREATE EXTENSION IF NOT EXISTS pgcrypto;
-- =============================================================================

CREATE EXTENSION IF NOT EXISTS pgcrypto;

BEGIN;

-- 1. Usuario en IAM
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
    version
)
SELECT
    '00000000-0000-0000-0000-000000000000',
    'especialista.test@techmarket.com',
    'especialista.test@techmarket.com',
    'Carlos',
    'Techero',
    '+591 70123456',
    'Bolivia',
    'La Paz',
    'ESPECIALISTA',
    TRUE,
    TRUE,
    0
WHERE NOT EXISTS (
    SELECT 1
    FROM iam_user
    WHERE tenant_id = '00000000-0000-0000-0000-000000000000'
      AND (
        LOWER(username) = LOWER('especialista.test@techmarket.com')
        OR LOWER(email)    = LOWER('especialista.test@techmarket.com')
      )
);

-- 2. Credencial (Contraseña: Especialista123!)
INSERT INTO iam_user_credential (user_id, tenant_id, password_hash)
SELECT
    u.id,
    '00000000-0000-0000-0000-000000000000',
    crypt('Especialista123!', gen_salt('bf', 10))
FROM iam_user u
WHERE u.tenant_id = '00000000-0000-0000-0000-000000000000'
  AND LOWER(u.username) = LOWER('especialista.test@techmarket.com')
  AND NOT EXISTS (
    SELECT 1 FROM iam_user_credential c WHERE c.user_id = u.id
  );

-- 3. Asignar rol 'especialista' (ya seeded en V7)
INSERT INTO iam_user_roles (user_id, role_id)
SELECT u.id, r.id
FROM iam_user u
JOIN iam_role r
  ON r.tenant_id = '00000000-0000-0000-0000-000000000000'
 AND LOWER(r.name) = LOWER('especialista')
WHERE u.tenant_id = '00000000-0000-0000-0000-000000000000'
  AND LOWER(u.username) = LOWER('especialista.test@techmarket.com')
  AND NOT EXISTS (
    SELECT 1
    FROM iam_user_roles ur
    WHERE ur.user_id = u.id
      AND ur.role_id = r.id
  );

-- 4. Scope global
INSERT INTO iam_user_scope (tenant_id, user_id, branch_id, scope_type)
SELECT
    '00000000-0000-0000-0000-000000000000',
    u.id,
    NULL,
    'GLOBAL'
FROM iam_user u
WHERE u.tenant_id = '00000000-0000-0000-0000-000000000000'
  AND LOWER(u.username) = LOWER('especialista.test@techmarket.com')
  AND NOT EXISTS (
    SELECT 1
    FROM iam_user_scope s
    WHERE s.user_id = u.id
      AND UPPER(s.scope_type) = 'GLOBAL'
  );

COMMIT;

-- Verificación rápida:
-- SELECT u.id, u.username, u.email, u.first_name, u.last_name, r.name AS rol
-- FROM iam_user u
-- JOIN iam_user_roles ur ON ur.user_id = u.id
-- JOIN iam_role r ON r.id = ur.role_id
-- WHERE LOWER(u.username) = 'especialista.test';
