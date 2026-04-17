-- Flyway Migration V4: Seed bootstrap ADMIN_GLOBAL role and user in global tenant.

INSERT INTO iam_role (tenant_id, name, description, version, hierarchy_level)
SELECT '00000000-0000-0000-0000-000000000000',
       'ADMIN_GLOBAL',
       'Administrador global con visibilidad cross-tenant',
       0,
       0
WHERE NOT EXISTS (
    SELECT 1
    FROM iam_role
    WHERE tenant_id = '00000000-0000-0000-0000-000000000000'
      AND LOWER(name) = LOWER('ADMIN_GLOBAL')
);

INSERT INTO iam_user (tenant_id, username, email, active, version)
SELECT '00000000-0000-0000-0000-000000000000',
       'admin.global',
       'admin.global@agrocore.com',
       TRUE,
       0
WHERE NOT EXISTS (
    SELECT 1
    FROM iam_user
    WHERE tenant_id = '00000000-0000-0000-0000-000000000000'
      AND (
        LOWER(username) = LOWER('admin.global')
        OR LOWER(email) = LOWER('admin.global@agrocore.com')
      )
);

INSERT INTO iam_user_credential (user_id, tenant_id, password_hash)
SELECT u.id,
       '00000000-0000-0000-0000-000000000000',
       '$2a$10$Hq1WejDGlv0fpyC6.VRyFeq6Gvgg3LkDGZ5lhgRGTPjbts8cUUaG.'
FROM iam_user u
WHERE u.tenant_id = '00000000-0000-0000-0000-000000000000'
  AND LOWER(u.username) = LOWER('admin.global')
  AND NOT EXISTS (
    SELECT 1
    FROM iam_user_credential c
    WHERE c.user_id = u.id
);

INSERT INTO iam_user_roles (user_id, role_id)
SELECT u.id,
       r.id
FROM iam_user u
JOIN iam_role r
  ON r.tenant_id = '00000000-0000-0000-0000-000000000000'
 AND LOWER(r.name) = LOWER('ADMIN_GLOBAL')
WHERE u.tenant_id = '00000000-0000-0000-0000-000000000000'
  AND LOWER(u.username) = LOWER('admin.global')
  AND NOT EXISTS (
    SELECT 1
    FROM iam_user_roles ur
    WHERE ur.user_id = u.id
      AND ur.role_id = r.id
);

INSERT INTO iam_user_scope (tenant_id, user_id, branch_id, scope_type)
SELECT '00000000-0000-0000-0000-000000000000',
       u.id,
       NULL,
       'GLOBAL'
FROM iam_user u
WHERE u.tenant_id = '00000000-0000-0000-0000-000000000000'
  AND LOWER(u.username) = LOWER('admin.global')
  AND NOT EXISTS (
    SELECT 1
    FROM iam_user_scope s
      WHERE s.user_id = u.id
      AND UPPER(s.scope_type) = 'GLOBAL'
);
