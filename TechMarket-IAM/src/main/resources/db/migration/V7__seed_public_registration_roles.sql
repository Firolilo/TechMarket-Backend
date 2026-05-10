INSERT INTO iam_role (tenant_id, name, description, version, hierarchy_level)
SELECT '00000000-0000-0000-0000-000000000000',
       'cliente',
       'Rol publico para clientes registrados',
       0,
       100
WHERE NOT EXISTS (
    SELECT 1
    FROM iam_role
    WHERE tenant_id = '00000000-0000-0000-0000-000000000000'
      AND LOWER(name) = LOWER('cliente')
);

INSERT INTO iam_role (tenant_id, name, description, version, hierarchy_level)
SELECT '00000000-0000-0000-0000-000000000000',
       'empresa',
       'Rol publico para empresas registradas',
       0,
       100
WHERE NOT EXISTS (
    SELECT 1
    FROM iam_role
    WHERE tenant_id = '00000000-0000-0000-0000-000000000000'
      AND LOWER(name) = LOWER('empresa')
);

INSERT INTO iam_role (tenant_id, name, description, version, hierarchy_level)
SELECT '00000000-0000-0000-0000-000000000000',
       'especialista',
       'Rol publico para especialistas registrados',
       0,
       100
WHERE NOT EXISTS (
    SELECT 1
    FROM iam_role
    WHERE tenant_id = '00000000-0000-0000-0000-000000000000'
      AND LOWER(name) = LOWER('especialista')
);
