-- =============================================================================
-- SEEDER DE PRUEBA: Empresa en TechMarket-IA
-- Base de datos: TechMarket-IA
-- Email:         empresa.test@techmarket.com
-- Contraseña:    Empresa123!
--
-- REQUISITO: pgcrypto habilitado.
--   CREATE EXTENSION IF NOT EXISTS pgcrypto;
--
-- UUIDs base:
--   Usuario:      bb000000-0000-0000-0000-000000000001
--   Tenant:       bb000000-0000-0000-0000-000000000002
--   Branch:       bb000000-0000-0000-0001-xxxxxxxxxxxxxx
--   Listings:     bb000000-0000-0000-0002-xxxxxxxxxxxxxx
--   Reviews:      bb000000-0000-0000-0003-xxxxxxxxxxxxxx
--   Feed posts:   bb000000-0000-0000-0004-xxxxxxxxxxxxxx
--   Feed media:   bb000000-0000-0000-0005-xxxxxxxxxxxxxx
-- =============================================================================

CREATE EXTENSION IF NOT EXISTS pgcrypto;

BEGIN;

-- ─────────────────────────────────────────────────────────────
-- 1. USUARIO (para author_user_id en posts, reviews, tenant_members)
-- ─────────────────────────────────────────────────────────────
INSERT INTO users (id, first_name, last_name, email, password_hash, status, created_at, updated_at)
SELECT
    'bb000000-0000-0000-0000-000000000001'::uuid,
    'TechStore',
    'Bolivia',
    'empresa.test@techmarket.com',
    crypt('Empresa123!', gen_salt('bf', 10)),
    'ACTIVE',
    NOW() - INTERVAL '4 months',
    NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE email = 'empresa.test@techmarket.com'
);

-- ─────────────────────────────────────────────────────────────
-- 2. CATEGORIAS DE NEGOCIO (reusa V96 si ya existen, no duplica)
-- ─────────────────────────────────────────────────────────────
INSERT INTO business_categories (id, name, description) VALUES
    ('20000000-0000-0000-0000-000000000101', 'Retail tecnologico',       'Empresas dedicadas a la venta de hardware, perifericos y accesorios.'),
    ('20000000-0000-0000-0000-000000000102', 'Servicios tecnicos',       'Empresas de soporte, reparacion, mantenimiento e implementacion tecnologica.'),
    ('20000000-0000-0000-0000-000000000103', 'Integradores corporativos','Proveedores de soluciones para oficinas, redes, seguridad e infraestructura.')
ON CONFLICT (id) DO NOTHING;

-- ─────────────────────────────────────────────────────────────
-- 3. CATEGORIAS DE CATALOGO
-- ─────────────────────────────────────────────────────────────
INSERT INTO catalog_categories (id, name, parent_category_id, item_type, slug) VALUES
    ('30000000-0000-0000-0000-000000000100', 'Computacion',           NULL,                                         'PRODUCT', 'computacion'),
    ('30000000-0000-0000-0000-000000000101', 'Laptops',               '30000000-0000-0000-0000-000000000100'::uuid, 'PRODUCT', 'laptops'),
    ('30000000-0000-0000-0000-000000000102', 'Componentes PC',        '30000000-0000-0000-0000-000000000100'::uuid, 'PRODUCT', 'componentes-pc'),
    ('30000000-0000-0000-0000-000000000103', 'Perifericos',           '30000000-0000-0000-0000-000000000100'::uuid, 'PRODUCT', 'perifericos'),
    ('30000000-0000-0000-0000-000000000104', 'Redes y conectividad',  NULL,                                         'PRODUCT', 'redes-conectividad'),
    ('30000000-0000-0000-0000-000000000105', 'Servicios tecnicos',    NULL,                                         'SERVICE', 'servicios-tecnicos'),
    ('30000000-0000-0000-0000-000000000106', 'Soporte y mantenimiento','30000000-0000-0000-0000-000000000105'::uuid,'SERVICE', 'soporte-mantenimiento'),
    ('30000000-0000-0000-0000-000000000107', 'Instalacion de redes',  '30000000-0000-0000-0000-000000000105'::uuid, 'SERVICE', 'instalacion-redes')
ON CONFLICT (id) DO NOTHING;

-- ─────────────────────────────────────────────────────────────
-- 4. MARCAS
-- ─────────────────────────────────────────────────────────────
INSERT INTO brands (id, name, slug, logo_url) VALUES
    ('40000000-0000-0000-0000-000000000101', 'Lenovo',   'lenovo',   'https://cdn.techmarket.local/brands/lenovo.png'),
    ('40000000-0000-0000-0000-000000000102', 'Asus',     'asus',     'https://cdn.techmarket.local/brands/asus.png'),
    ('40000000-0000-0000-0000-000000000103', 'Logitech', 'logitech', 'https://cdn.techmarket.local/brands/logitech.png'),
    ('40000000-0000-0000-0000-000000000104', 'TP-Link',  'tp-link',  'https://cdn.techmarket.local/brands/tp-link.png'),
    ('40000000-0000-0000-0000-000000000105', 'Kingston', 'kingston', 'https://cdn.techmarket.local/brands/kingston.png')
ON CONFLICT (id) DO NOTHING;

-- ─────────────────────────────────────────────────────────────
-- 5. TENANT (empresa)
-- ─────────────────────────────────────────────────────────────
INSERT INTO tenants (id, business_name, legal_name, tax_id, business_type, description, status, registered_at, created_at, updated_at)
SELECT
    'bb000000-0000-0000-0000-000000000002'::uuid,
    'TechStore Bolivia',
    'TechStore Bolivia SRL',
    'bb000000-0000-0000-0000-000000000099'::uuid,
    'RETAIL',
    'Tienda especializada en laptops, componentes, perifericos y accesorios para usuarios profesionales y gamers.',
    'ACTIVE',
    NOW() - INTERVAL '4 months',
    NOW() - INTERVAL '4 months',
    NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM tenants WHERE id = 'bb000000-0000-0000-0000-000000000002'::uuid
);

-- ─────────────────────────────────────────────────────────────
-- 6. PERFIL DE EMPRESA
-- ─────────────────────────────────────────────────────────────
INSERT INTO tenant_profiles (
    id, tenant_id, logo_url, cover_image_url,
    short_description, full_description,
    website_url, facebook_url, instagram_url,
    rating_average, reviews_count
)
SELECT
    'bb000000-0000-0000-0000-000000000003'::uuid,
    'bb000000-0000-0000-0000-000000000002'::uuid,
    'https://cdn.techmarket.local/tenants/techstore/logo.png',
    'https://cdn.techmarket.local/tenants/techstore/cover.jpg',
    'Hardware, perifericos y accesorios con disponibilidad local.',
    'Catalogo curado de laptops, componentes y accesorios con stock local, garantia y soporte tecnico post-venta.',
    'https://techstore-bolivia.example.local',
    'https://facebook.com/techstoreboliviaseed',
    'https://instagram.com/techstoreboliviaseed',
    4.65,
    24
WHERE NOT EXISTS (
    SELECT 1 FROM tenant_profiles WHERE id = 'bb000000-0000-0000-0000-000000000003'::uuid
);

-- ─────────────────────────────────────────────────────────────
-- 7. MIEMBRO (vincula usuario con el tenant como OWNER)
-- ─────────────────────────────────────────────────────────────
INSERT INTO tenant_members (id, tenant_id, user_id, tenant_role, status, invited_by_user_id, joined_at)
SELECT
    'bb000000-0000-0000-0000-000000000004'::uuid,
    'bb000000-0000-0000-0000-000000000002'::uuid,
    'bb000000-0000-0000-0000-000000000001'::uuid,
    'OWNER',
    'ACTIVE',
    NULL,
    NOW() - INTERVAL '4 months'
WHERE NOT EXISTS (
    SELECT 1 FROM tenant_members WHERE id = 'bb000000-0000-0000-0000-000000000004'::uuid
);

-- ─────────────────────────────────────────────────────────────
-- 8. SUCURSAL PRINCIPAL
-- ─────────────────────────────────────────────────────────────
INSERT INTO branches (id, tenant_id, name, address, city, latitude, longitude, phone, opening_hours, is_main_branch, status, created_at)
SELECT
    'bb000000-0000-0000-0001-000000000001'::uuid,
    'bb000000-0000-0000-0000-000000000002'::uuid,
    'TechStore Bolivia — Sucursal Central',
    'Av. Arce 2450, Sopocachi',
    'La Paz',
    -16.509240,
    -68.122719,
    '+591 70100200',
    'Lun-Vie 09:00-19:00; Sab 09:00-14:00',
    TRUE,
    'ACTIVE',
    NOW() - INTERVAL '4 months'
WHERE NOT EXISTS (
    SELECT 1 FROM branches WHERE id = 'bb000000-0000-0000-0001-000000000001'::uuid
);

INSERT INTO branches (id, tenant_id, name, address, city, latitude, longitude, phone, opening_hours, is_main_branch, status, created_at)
SELECT
    'bb000000-0000-0000-0001-000000000002'::uuid,
    'bb000000-0000-0000-0000-000000000002'::uuid,
    'TechStore Bolivia — Zona Sur',
    'Calle 21 de Calacoto 7880, Calacoto',
    'La Paz',
    -16.539091,
    -68.079017,
    '+591 70100201',
    'Lun-Sab 10:00-20:00',
    FALSE,
    'ACTIVE',
    NOW() - INTERVAL '2 months'
WHERE NOT EXISTS (
    SELECT 1 FROM branches WHERE id = 'bb000000-0000-0000-0001-000000000002'::uuid
);

-- ─────────────────────────────────────────────────────────────
-- 9. LISTINGS (4 productos + 2 servicios)
-- ─────────────────────────────────────────────────────────────
INSERT INTO listings (id, tenant_id, category_id, brand_id, listing_type, title, description, base_price, currency, status, is_visible, internal_sku, created_at, updated_at)
VALUES
    ('bb000000-0000-0000-0002-000000000001'::uuid,
     'bb000000-0000-0000-0000-000000000002'::uuid,
     '30000000-0000-0000-0000-000000000101'::uuid,
     '40000000-0000-0000-0000-000000000101'::uuid,
     'PRODUCT', 'Lenovo ThinkPad E14 Gen 5 Ryzen 7',
     'Laptop empresarial 14" con procesador Ryzen 7 7730U, 16 GB RAM DDR4, SSD 512 GB NVMe y Windows 11 Pro.',
     7350.00, 'BOB', 'ACTIVE', TRUE, 'TSB-LEN-E14-R7',
     NOW() - INTERVAL '3 months', NOW()),

    ('bb000000-0000-0000-0002-000000000002'::uuid,
     'bb000000-0000-0000-0000-000000000002'::uuid,
     '30000000-0000-0000-0000-000000000101'::uuid,
     '40000000-0000-0000-0000-000000000102'::uuid,
     'PRODUCT', 'Asus VivoBook 15 Core i5',
     'Laptop 15.6" FHD con Intel Core i5-1235U, 8 GB RAM, SSD 256 GB y pantalla IPS antibrillo.',
     4890.00, 'BOB', 'ACTIVE', TRUE, 'TSB-ASU-VB15-I5',
     NOW() - INTERVAL '2 months', NOW()),

    ('bb000000-0000-0000-0002-000000000003'::uuid,
     'bb000000-0000-0000-0000-000000000002'::uuid,
     '30000000-0000-0000-0000-000000000102'::uuid,
     '40000000-0000-0000-0000-000000000105'::uuid,
     'PRODUCT', 'Kingston FURY Beast DDR5 16 GB',
     'Modulo de memoria RAM DDR5 5200 MHz CL40 de alto rendimiento para PCs de escritorio.',
     780.00, 'BOB', 'ACTIVE', TRUE, 'TSB-KIN-FURY-DDR5-16',
     NOW() - INTERVAL '6 weeks', NOW()),

    ('bb000000-0000-0000-0002-000000000004'::uuid,
     'bb000000-0000-0000-0000-000000000002'::uuid,
     '30000000-0000-0000-0000-000000000103'::uuid,
     '40000000-0000-0000-0000-000000000103'::uuid,
     'PRODUCT', 'Logitech MX Master 3S',
     'Mouse ergonomico premium con sensor de 8000 DPI, rueda MagSpeed silenciosa y conectividad multi-dispositivo.',
     620.00, 'BOB', 'ACTIVE', TRUE, 'TSB-LOG-MXM3S',
     NOW() - INTERVAL '1 month', NOW()),

    ('bb000000-0000-0000-0002-000000000005'::uuid,
     'bb000000-0000-0000-0000-000000000002'::uuid,
     '30000000-0000-0000-0000-000000000106'::uuid,
     NULL,
     'SERVICE', 'Mantenimiento preventivo de laptop',
     'Limpieza interna, cambio de pasta termica, revision de disco, memoria, temperatura y diagnostico general.',
     180.00, 'BOB', 'ACTIVE', TRUE, 'TSB-SRV-MANT-LAP',
     NOW() - INTERVAL '3 months', NOW()),

    ('bb000000-0000-0000-0002-000000000006'::uuid,
     'bb000000-0000-0000-0000-000000000002'::uuid,
     '30000000-0000-0000-0000-000000000106'::uuid,
     NULL,
     'SERVICE', 'Upgrade de SSD y migracion de datos',
     'Instalacion de SSD NVMe, clonacion del sistema operativo, prueba de rendimiento y verificacion de integridad.',
     250.00, 'BOB', 'ACTIVE', TRUE, 'TSB-SRV-UPGRADE-SSD',
     NOW() - INTERVAL '2 months', NOW())
ON CONFLICT (id) DO NOTHING;

-- ─────────────────────────────────────────────────────────────
-- 10. BRANCH LISTINGS (productos disponibles en sucursales)
-- ─────────────────────────────────────────────────────────────
INSERT INTO branch_listings (id, branch_id, listing_id, status, is_available, created_at)
VALUES
    ('bb000000-0000-0000-0002-000000000011'::uuid, 'bb000000-0000-0000-0001-000000000001'::uuid, 'bb000000-0000-0000-0002-000000000001'::uuid, 'ACTIVE', TRUE, NOW()),
    ('bb000000-0000-0000-0002-000000000012'::uuid, 'bb000000-0000-0000-0001-000000000001'::uuid, 'bb000000-0000-0000-0002-000000000002'::uuid, 'ACTIVE', TRUE, NOW()),
    ('bb000000-0000-0000-0002-000000000013'::uuid, 'bb000000-0000-0000-0001-000000000001'::uuid, 'bb000000-0000-0000-0002-000000000003'::uuid, 'ACTIVE', TRUE, NOW()),
    ('bb000000-0000-0000-0002-000000000014'::uuid, 'bb000000-0000-0000-0001-000000000001'::uuid, 'bb000000-0000-0000-0002-000000000004'::uuid, 'ACTIVE', TRUE, NOW()),
    ('bb000000-0000-0000-0002-000000000015'::uuid, 'bb000000-0000-0000-0001-000000000002'::uuid, 'bb000000-0000-0000-0002-000000000001'::uuid, 'ACTIVE', TRUE, NOW()),
    ('bb000000-0000-0000-0002-000000000016'::uuid, 'bb000000-0000-0000-0001-000000000002'::uuid, 'bb000000-0000-0000-0002-000000000002'::uuid, 'ACTIVE', TRUE, NOW()),
    ('bb000000-0000-0000-0002-000000000017'::uuid, 'bb000000-0000-0000-0001-000000000001'::uuid, 'bb000000-0000-0000-0002-000000000005'::uuid, 'ACTIVE', TRUE, NOW()),
    ('bb000000-0000-0000-0002-000000000018'::uuid, 'bb000000-0000-0000-0001-000000000001'::uuid, 'bb000000-0000-0000-0002-000000000006'::uuid, 'ACTIVE', TRUE, NOW())
ON CONFLICT (id) DO NOTHING;

-- ─────────────────────────────────────────────────────────────
-- 11. IMAGENES DE LISTINGS
-- ─────────────────────────────────────────────────────────────
INSERT INTO listing_images (id, listing_id, image_url, display_order, is_primary)
VALUES
    ('bb000000-0000-0000-0002-000000000021'::uuid, 'bb000000-0000-0000-0002-000000000001'::uuid, 'https://cdn.techmarket.local/listings/thinkpad-e14/main.jpg',     '1', TRUE),
    ('bb000000-0000-0000-0002-000000000022'::uuid, 'bb000000-0000-0000-0002-000000000001'::uuid, 'https://cdn.techmarket.local/listings/thinkpad-e14/side.jpg',     '2', FALSE),
    ('bb000000-0000-0000-0002-000000000023'::uuid, 'bb000000-0000-0000-0002-000000000002'::uuid, 'https://cdn.techmarket.local/listings/vivobook-15/main.jpg',      '1', TRUE),
    ('bb000000-0000-0000-0002-000000000024'::uuid, 'bb000000-0000-0000-0002-000000000003'::uuid, 'https://cdn.techmarket.local/listings/kingston-fury-ddr5/main.jpg','1', TRUE),
    ('bb000000-0000-0000-0002-000000000025'::uuid, 'bb000000-0000-0000-0002-000000000004'::uuid, 'https://cdn.techmarket.local/listings/mx-master-3s/main.jpg',    '1', TRUE),
    ('bb000000-0000-0000-0002-000000000026'::uuid, 'bb000000-0000-0000-0002-000000000005'::uuid, 'https://cdn.techmarket.local/listings/mantenimiento-laptop/main.jpg','1', TRUE),
    ('bb000000-0000-0000-0002-000000000027'::uuid, 'bb000000-0000-0000-0002-000000000006'::uuid, 'https://cdn.techmarket.local/listings/upgrade-ssd/main.jpg',     '1', TRUE)
ON CONFLICT (id) DO NOTHING;

-- ─────────────────────────────────────────────────────────────
-- 12. ESPECIFICACIONES DE LISTINGS
-- ─────────────────────────────────────────────────────────────
INSERT INTO listing_specifications (id, listing_id, attribute_name, attribute_value, unit, is_normalized)
VALUES
    ('bb000000-0000-0000-0002-000000000031'::uuid, 'bb000000-0000-0000-0002-000000000001'::uuid, 'Procesador',    'AMD Ryzen 7 7730U',    NULL,   TRUE),
    ('bb000000-0000-0000-0002-000000000032'::uuid, 'bb000000-0000-0000-0002-000000000001'::uuid, 'Memoria RAM',   '16',                   'GB',   TRUE),
    ('bb000000-0000-0000-0002-000000000033'::uuid, 'bb000000-0000-0000-0002-000000000001'::uuid, 'Almacenamiento','512',                  'GB SSD', TRUE),
    ('bb000000-0000-0000-0002-000000000034'::uuid, 'bb000000-0000-0000-0002-000000000002'::uuid, 'Procesador',    'Intel Core i5-1235U',  NULL,   TRUE),
    ('bb000000-0000-0000-0002-000000000035'::uuid, 'bb000000-0000-0000-0002-000000000002'::uuid, 'Pantalla',      '15.6 FHD IPS',         NULL,   TRUE),
    ('bb000000-0000-0000-0002-000000000036'::uuid, 'bb000000-0000-0000-0002-000000000003'::uuid, 'Velocidad',     '5200',                 'MHz',  TRUE),
    ('bb000000-0000-0000-0002-000000000037'::uuid, 'bb000000-0000-0000-0002-000000000003'::uuid, 'Capacidad',     '16',                   'GB',   TRUE),
    ('bb000000-0000-0000-0002-000000000038'::uuid, 'bb000000-0000-0000-0002-000000000004'::uuid, 'DPI',           '8000',                 NULL,   TRUE),
    ('bb000000-0000-0000-0002-000000000039'::uuid, 'bb000000-0000-0000-0002-000000000004'::uuid, 'Conectividad',  'Bluetooth, USB-C',     NULL,   TRUE)
ON CONFLICT (id) DO NOTHING;

-- ─────────────────────────────────────────────────────────────
-- 13. INVENTARIO EN SUCURSALES
-- ─────────────────────────────────────────────────────────────
INSERT INTO branch_inventory (id, listing_id, branch_id, stock_available, stock_reserved, minimum_stock, updated_at)
VALUES
    ('bb000000-0000-0000-0002-000000000041'::uuid, 'bb000000-0000-0000-0002-000000000001'::uuid, 'bb000000-0000-0000-0001-000000000001'::uuid, '6',  '1', '2', NOW()),
    ('bb000000-0000-0000-0002-000000000042'::uuid, 'bb000000-0000-0000-0002-000000000001'::uuid, 'bb000000-0000-0000-0001-000000000002'::uuid, '4',  '0', '1', NOW()),
    ('bb000000-0000-0000-0002-000000000043'::uuid, 'bb000000-0000-0000-0002-000000000002'::uuid, 'bb000000-0000-0000-0001-000000000001'::uuid, '9',  '2', '3', NOW()),
    ('bb000000-0000-0000-0002-000000000044'::uuid, 'bb000000-0000-0000-0002-000000000002'::uuid, 'bb000000-0000-0000-0001-000000000002'::uuid, '5',  '1', '2', NOW()),
    ('bb000000-0000-0000-0002-000000000045'::uuid, 'bb000000-0000-0000-0002-000000000003'::uuid, 'bb000000-0000-0000-0001-000000000001'::uuid, '18', '3', '5', NOW()),
    ('bb000000-0000-0000-0002-000000000046'::uuid, 'bb000000-0000-0000-0002-000000000004'::uuid, 'bb000000-0000-0000-0001-000000000001'::uuid, '11', '2', '3', NOW())
ON CONFLICT (id) DO NOTHING;

-- ─────────────────────────────────────────────────────────────
-- 14. DETALLES DE SERVICIOS
-- ─────────────────────────────────────────────────────────────
INSERT INTO service_details (id, listing_id, estimated_duration_minutes, requires_diagnosis, offers_on_site_service, service_area, terms_and_conditions)
VALUES
    ('bb000000-0000-0000-0002-000000000051'::uuid, 'bb000000-0000-0000-0002-000000000005'::uuid,
     90, 'false', 'true', 'La Paz y El Alto',
     'Incluye limpieza y diagnostico basico. Repuestos se cotizan por separado.'),
    ('bb000000-0000-0000-0002-000000000052'::uuid, 'bb000000-0000-0000-0002-000000000006'::uuid,
     120, 'true', 'false', 'La Paz',
     'Precio base incluye mano de obra. SSD no incluido; se cotiza segun modelo del equipo.')
ON CONFLICT (id) DO NOTHING;

-- ─────────────────────────────────────────────────────────────
-- 15. RESEÑAS (de clientes sobre la empresa)
-- ─────────────────────────────────────────────────────────────
INSERT INTO reviews (id, tenant_id, user_id, ticket_id, quote_id, rating, comment, moderation_status, created_at)
VALUES
    ('bb000000-0000-0000-0003-000000000001'::uuid,
     'bb000000-0000-0000-0000-000000000002'::uuid,
     '10000000-0000-0000-0000-000000000101'::uuid,
     NULL, NULL, 4.5,
     'Excelente atencion y el equipo llego en perfectas condiciones. Lo recomiendo.',
     'APPROVED', NOW() - INTERVAL '3 months'),

    ('bb000000-0000-0000-0003-000000000002'::uuid,
     'bb000000-0000-0000-0000-000000000002'::uuid,
     '10000000-0000-0000-0000-000000000102'::uuid,
     NULL, NULL, 5.0,
     'Compre un laptop ThinkPad y el servicio post-venta es muy bueno. Respondieron todas mis dudas.',
     'APPROVED', NOW() - INTERVAL '2 months'),

    ('bb000000-0000-0000-0003-000000000003'::uuid,
     'bb000000-0000-0000-0000-000000000002'::uuid,
     '10000000-0000-0000-0000-000000000103'::uuid,
     NULL, NULL, 4.0,
     'Buenos productos y precios competitivos. La entrega tardo un dia mas de lo esperado pero el producto bien.',
     'APPROVED', NOW() - INTERVAL '6 weeks'),

    ('bb000000-0000-0000-0003-000000000004'::uuid,
     'bb000000-0000-0000-0000-000000000002'::uuid,
     '10000000-0000-0000-0000-000000000104'::uuid,
     NULL, NULL, 5.0,
     'El mantenimiento de laptop fue rapido y profesional. Me explicaron todo lo que encontraron.',
     'APPROVED', NOW() - INTERVAL '3 weeks'),

    ('bb000000-0000-0000-0003-000000000005'::uuid,
     'bb000000-0000-0000-0000-000000000002'::uuid,
     '10000000-0000-0000-0000-000000000101'::uuid,
     NULL, NULL, 4.5,
     'Gran variedad de perifericos. Encontre el mouse que buscaba a buen precio.',
     'APPROVED', NOW() - INTERVAL '2 weeks')
ON CONFLICT (id) DO NOTHING;

-- ─────────────────────────────────────────────────────────────
-- 16. PUBLICACIONES (feed_posts)
-- ─────────────────────────────────────────────────────────────
INSERT INTO feed_posts (id, tenant_id, author_user_id, post_type, title, content, status, created_at)
VALUES
    ('bb000000-0000-0000-0004-000000000001'::uuid,
     'bb000000-0000-0000-0000-000000000002'::uuid,
     'bb000000-0000-0000-0000-000000000001'::uuid,
     'ANNOUNCEMENT',
     'Nueva sucursal en Zona Sur',
     'Nos complace anunciar la apertura de nuestra segunda sucursal en Calacoto. Mas stock, misma calidad de atencion.',
     'PUBLISHED', NOW() - INTERVAL '2 months'),

    ('bb000000-0000-0000-0004-000000000002'::uuid,
     'bb000000-0000-0000-0000-000000000002'::uuid,
     'bb000000-0000-0000-0000-000000000001'::uuid,
     'PRODUCT',
     'Laptop Lenovo ThinkPad E14 Gen 5 disponible',
     'Ya tenemos en stock el nuevo ThinkPad E14 Gen 5 con Ryzen 7. Ideal para trabajo profesional y alto rendimiento. Consulta disponibilidad.',
     'PUBLISHED', NOW() - INTERVAL '6 weeks'),

    ('bb000000-0000-0000-0004-000000000003'::uuid,
     'bb000000-0000-0000-0000-000000000002'::uuid,
     'bb000000-0000-0000-0000-000000000001'::uuid,
     'SERVICE',
     'Servicio de upgrade de SSD con migracion',
     'Dale nueva vida a tu laptop. Realizamos upgrade de SSD con migracion completa de datos. Sin perder tu informacion, sin reinstalar.',
     'PUBLISHED', NOW() - INTERVAL '1 month'),

    ('bb000000-0000-0000-0004-000000000004'::uuid,
     'bb000000-0000-0000-0000-000000000002'::uuid,
     'bb000000-0000-0000-0000-000000000001'::uuid,
     'PROMOTION',
     'Descuento en perifericos Logitech este mes',
     '10% de descuento en toda la linea de perifericos Logitech durante este mes. Stock limitado en ambas sucursales.',
     'PUBLISHED', NOW() - INTERVAL '2 weeks'),

    ('bb000000-0000-0000-0004-000000000005'::uuid,
     'bb000000-0000-0000-0000-000000000002'::uuid,
     'bb000000-0000-0000-0000-000000000001'::uuid,
     'TIP',
     'Cuando cambiar la pasta termica de tu laptop',
     'Si tu laptop se calienta mas de lo normal o el ventilador trabaja al maximo, probablemente necesitas cambiar la pasta termica. Visita nuestro servicio tecnico para un diagnostico gratuito.',
     'PUBLISHED', NOW() - INTERVAL '5 days')
ON CONFLICT (id) DO NOTHING;

-- ─────────────────────────────────────────────────────────────
-- 17. MEDIA DE PUBLICACIONES
-- ─────────────────────────────────────────────────────────────
INSERT INTO feed_media (id, feed_post_id, media_url, media_type, display_order)
VALUES
    ('bb000000-0000-0000-0005-000000000001'::uuid, 'bb000000-0000-0000-0004-000000000001'::uuid,
     'https://cdn.techmarket.local/posts/sucursal-zona-sur.jpg', 'IMAGE', '1'),
    ('bb000000-0000-0000-0005-000000000002'::uuid, 'bb000000-0000-0000-0004-000000000002'::uuid,
     'https://cdn.techmarket.local/posts/thinkpad-e14-promo.jpg', 'IMAGE', '1'),
    ('bb000000-0000-0000-0005-000000000003'::uuid, 'bb000000-0000-0000-0004-000000000003'::uuid,
     'https://cdn.techmarket.local/posts/upgrade-ssd-servicio.jpg', 'IMAGE', '1'),
    ('bb000000-0000-0000-0005-000000000004'::uuid, 'bb000000-0000-0000-0004-000000000004'::uuid,
     'https://cdn.techmarket.local/posts/logitech-descuento.jpg', 'IMAGE', '1'),
    ('bb000000-0000-0000-0005-000000000005'::uuid, 'bb000000-0000-0000-0004-000000000005'::uuid,
     'https://cdn.techmarket.local/posts/pasta-termica-tip.jpg', 'IMAGE', '1')
ON CONFLICT (id) DO NOTHING;

COMMIT;

-- =============================================================================
-- Verificacion rapida:
-- SELECT id, email, status FROM users WHERE email = 'empresa.test@techmarket.com';
-- SELECT id, business_name, status FROM tenants WHERE id = 'bb000000-0000-0000-0000-000000000002';
-- SELECT COUNT(*) FROM listings WHERE tenant_id = 'bb000000-0000-0000-0000-000000000002';
-- SELECT COUNT(*) FROM reviews  WHERE tenant_id = 'bb000000-0000-0000-0000-000000000002';
-- SELECT COUNT(*) FROM feed_posts WHERE tenant_id = 'bb000000-0000-0000-0000-000000000002';
-- =============================================================================
