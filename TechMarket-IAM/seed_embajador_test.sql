-- =============================================================================
-- SEEDER DE PRUEBA: Embajador en TechMarket-IAM
-- Base de datos: TechMarket-IAM (microservicio de autenticación)
-- Email:         embajador.test@techmarket.com
-- Contraseña:    Embajador123!
--
-- REQUISITO: pgcrypto habilitado.
--   CREATE EXTENSION IF NOT EXISTS pgcrypto;
-- =============================================================================

CREATE EXTENSION IF NOT EXISTS pgcrypto;

BEGIN;

-- 1. Rol embajador (no está en V7, hay que crearlo si no existe)
INSERT INTO iam_role (tenant_id, name, description, version, hierarchy_level)
SELECT
    '00000000-0000-0000-0000-000000000000',
    'embajador',
    'Rol para embajadores de TechMarket',
    0,
    100
WHERE NOT EXISTS (
    SELECT 1 FROM iam_role
    WHERE tenant_id = '00000000-0000-0000-0000-000000000000'
      AND LOWER(name) = LOWER('embajador')
);

-- 2. Usuario en IAM
INSERT INTO iam_user (
    tenant_id, username, email, first_name, last_name,
    phone, country, city, user_type, terms_accepted, active, version
)
SELECT
    '00000000-0000-0000-0000-000000000000',
    'embajador.test@techmarket.com',
    'embajador.test@techmarket.com',
    'Lucas',
    'Embajador',
    '+591 70000000',
    'Bolivia',
    'Santa Cruz',
    'embajador',
    TRUE,
    TRUE,
    0
WHERE NOT EXISTS (
    SELECT 1 FROM iam_user
    WHERE tenant_id = '00000000-0000-0000-0000-000000000000'
      AND (
        LOWER(username) = LOWER('embajador.test@techmarket.com')
        OR LOWER(email)  = LOWER('embajador.test@techmarket.com')
      )
);

-- 3. Credencial (Contraseña: Embajador123!)
INSERT INTO iam_user_credential (user_id, tenant_id, password_hash)
SELECT
    u.id,
    '00000000-0000-0000-0000-000000000000',
    crypt('Embajador123!', gen_salt('bf', 10))
FROM iam_user u
WHERE u.tenant_id = '00000000-0000-0000-0000-000000000000'
  AND LOWER(u.username) = LOWER('embajador.test@techmarket.com')
  AND NOT EXISTS (
    SELECT 1 FROM iam_user_credential c WHERE c.user_id = u.id
  );

-- 4. Asignar rol embajador
INSERT INTO iam_user_roles (user_id, role_id)
SELECT u.id, r.id
FROM iam_user u
JOIN iam_role r
  ON r.tenant_id = '00000000-0000-0000-0000-000000000000'
 AND LOWER(r.name) = LOWER('embajador')
WHERE u.tenant_id = '00000000-0000-0000-0000-000000000000'
  AND LOWER(u.username) = LOWER('embajador.test@techmarket.com')
  AND NOT EXISTS (
    SELECT 1 FROM iam_user_roles ur
    WHERE ur.user_id = u.id AND ur.role_id = r.id
  );

-- 5. Scope global
INSERT INTO iam_user_scope (tenant_id, user_id, branch_id, scope_type)
SELECT
    '00000000-0000-0000-0000-000000000000',
    u.id,
    NULL,
    'GLOBAL'
FROM iam_user u
WHERE u.tenant_id = '00000000-0000-0000-0000-000000000000'
  AND LOWER(u.username) = LOWER('embajador.test@techmarket.com')
  AND NOT EXISTS (
    SELECT 1 FROM iam_user_scope s
    WHERE s.user_id = u.id AND UPPER(s.scope_type) = 'GLOBAL'
  );

COMMIT;

-- Verificación rápida:
-- SELECT u.id, u.username, u.email, u.user_type, r.name AS rol
-- FROM iam_user u
-- JOIN iam_user_roles ur ON ur.user_id = u.id
-- JOIN iam_role r ON r.id = ur.role_id
-- WHERE LOWER(u.username) = 'embajador.test@techmarket.com';
