INSERT INTO users (
    id,
    first_name,
    last_name,
    email,
    phone,
    password_hash,
    status,
    last_login_at,
    created_at,
    updated_at
) VALUES
    (
        '10000000-0000-0000-0000-000000000101',
        'Valeria',
        'Mendoza',
        'valeria.mendoza.seed@techmarket.local',
        '+59170001001',
        'seed-user',
        'ACTIVE',
        NULL,
        NOW(),
        NOW()
    ),
    (
        '10000000-0000-0000-0000-000000000102',
        'Diego',
        'Rojas',
        'diego.rojas.seed@techmarket.local',
        '+59170001002',
        'seed-user',
        'ACTIVE',
        NULL,
        NOW(),
        NOW()
    ),
    (
        '10000000-0000-0000-0000-000000000103',
        'Camila',
        'Quiroga',
        'camila.quiroga.seed@techmarket.local',
        '+59170001003',
        'seed-user',
        'ACTIVE',
        NULL,
        NOW(),
        NOW()
    ),
    (
        '10000000-0000-0000-0000-000000000104',
        'Marco',
        'Salvatierra',
        'marco.salvatierra.seed@techmarket.local',
        '+59170001004',
        'seed-user',
        'ACTIVE',
        NULL,
        NOW(),
        NOW()
    )
ON CONFLICT (id) DO UPDATE SET
    first_name = EXCLUDED.first_name,
    last_name = EXCLUDED.last_name,
    phone = EXCLUDED.phone,
    status = EXCLUDED.status,
    updated_at = NOW();

INSERT INTO business_categories (id, name, description) VALUES
    (
        '20000000-0000-0000-0000-000000000101',
        'Retail tecnologico',
        'Empresas dedicadas a la venta de hardware, perifericos y accesorios.'
    ),
    (
        '20000000-0000-0000-0000-000000000102',
        'Servicios tecnicos',
        'Empresas de soporte, reparacion, mantenimiento e implementacion tecnologica.'
    ),
    (
        '20000000-0000-0000-0000-000000000103',
        'Integradores corporativos',
        'Proveedores de soluciones para oficinas, redes, seguridad e infraestructura.'
    )
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    description = EXCLUDED.description;

INSERT INTO catalog_categories (id, name, parent_category_id, item_type, slug) VALUES
    ('30000000-0000-0000-0000-000000000100', 'Computacion', NULL, 'PRODUCT', 'computacion'),
    ('30000000-0000-0000-0000-000000000101', 'Laptops', '30000000-0000-0000-0000-000000000100', 'PRODUCT', 'laptops'),
    ('30000000-0000-0000-0000-000000000102', 'Componentes PC', '30000000-0000-0000-0000-000000000100', 'PRODUCT', 'componentes-pc'),
    ('30000000-0000-0000-0000-000000000103', 'Perifericos', '30000000-0000-0000-0000-000000000100', 'PRODUCT', 'perifericos'),
    ('30000000-0000-0000-0000-000000000104', 'Redes y conectividad', NULL, 'PRODUCT', 'redes-conectividad'),
    ('30000000-0000-0000-0000-000000000105', 'Servicios tecnicos', NULL, 'SERVICE', 'servicios-tecnicos'),
    ('30000000-0000-0000-0000-000000000106', 'Soporte y mantenimiento', '30000000-0000-0000-0000-000000000105', 'SERVICE', 'soporte-mantenimiento'),
    ('30000000-0000-0000-0000-000000000107', 'Instalacion de redes', '30000000-0000-0000-0000-000000000105', 'SERVICE', 'instalacion-redes')
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    parent_category_id = EXCLUDED.parent_category_id,
    item_type = EXCLUDED.item_type,
    slug = EXCLUDED.slug;

INSERT INTO brands (id, name, slug, logo_url) VALUES
    ('40000000-0000-0000-0000-000000000101', 'Lenovo', 'lenovo', 'https://cdn.techmarket.local/brands/lenovo.png'),
    ('40000000-0000-0000-0000-000000000102', 'Asus', 'asus', 'https://cdn.techmarket.local/brands/asus.png'),
    ('40000000-0000-0000-0000-000000000103', 'Logitech', 'logitech', 'https://cdn.techmarket.local/brands/logitech.png'),
    ('40000000-0000-0000-0000-000000000104', 'TP-Link', 'tp-link', 'https://cdn.techmarket.local/brands/tp-link.png'),
    ('40000000-0000-0000-0000-000000000105', 'Kingston', 'kingston', 'https://cdn.techmarket.local/brands/kingston.png')
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    slug = EXCLUDED.slug,
    logo_url = EXCLUDED.logo_url;

INSERT INTO tenants (
    id,
    business_name,
    legal_name,
    tax_id,
    business_type,
    description,
    status,
    registered_at,
    created_at,
    updated_at
) VALUES
    (
        '50000000-0000-0000-0000-000000000101',
        'Andes Tech Store',
        'Andes Tech Store SRL',
        'SEED-NIT-500101',
        'RETAIL',
        'Tienda especializada en laptops, componentes y perifericos para usuarios profesionales.',
        'ACTIVE',
        NOW() - INTERVAL '90 days',
        NOW() - INTERVAL '90 days',
        NOW()
    ),
    (
        '50000000-0000-0000-0000-000000000102',
        'ByteLab Servicios',
        'ByteLab Servicios Integrales SRL',
        'SEED-NIT-500102',
        'SERVICE_PROVIDER',
        'Soporte tecnico, mantenimiento preventivo y reparacion de equipos para hogares y empresas.',
        'ACTIVE',
        NOW() - INTERVAL '75 days',
        NOW() - INTERVAL '75 days',
        NOW()
    ),
    (
        '50000000-0000-0000-0000-000000000103',
        'Nexo Redes Bolivia',
        'Nexo Redes Bolivia SA',
        'SEED-NIT-500103',
        'INTEGRATOR',
        'Integrador de redes, conectividad, cableado estructurado y seguridad perimetral.',
        'ACTIVE',
        NOW() - INTERVAL '60 days',
        NOW() - INTERVAL '60 days',
        NOW()
    )
ON CONFLICT (id) DO UPDATE SET
    business_name = EXCLUDED.business_name,
    legal_name = EXCLUDED.legal_name,
    tax_id = EXCLUDED.tax_id,
    business_type = EXCLUDED.business_type,
    description = EXCLUDED.description,
    status = EXCLUDED.status,
    updated_at = NOW();

INSERT INTO tenant_profiles (
    id,
    tenant_id,
    logo_url,
    cover_image_url,
    short_description,
    full_description,
    website_url,
    facebook_url,
    instagram_url,
    rating_average,
    reviews_count
) VALUES
    (
        '51000000-0000-0000-0000-000000000101',
        '50000000-0000-0000-0000-000000000101',
        'https://cdn.techmarket.local/tenants/andes-tech/logo.png',
        'https://cdn.techmarket.local/tenants/andes-tech/cover.jpg',
        'Hardware y perifericos para trabajo, estudio y gaming.',
        'Catalogo curado de laptops, componentes y accesorios con disponibilidad local.',
        'https://andes-tech.example.local',
        'https://facebook.com/andestechseed',
        'https://instagram.com/andestechseed',
        4.70,
        38
    ),
    (
        '51000000-0000-0000-0000-000000000102',
        '50000000-0000-0000-0000-000000000102',
        'https://cdn.techmarket.local/tenants/bytelab/logo.png',
        'https://cdn.techmarket.local/tenants/bytelab/cover.jpg',
        'Servicio tecnico rapido para computadoras y notebooks.',
        'Diagnostico, mantenimiento, upgrades y soporte remoto con agenda flexible.',
        'https://bytelab.example.local',
        'https://facebook.com/bytelabseed',
        'https://instagram.com/bytelabseed',
        4.85,
        52
    ),
    (
        '51000000-0000-0000-0000-000000000103',
        '50000000-0000-0000-0000-000000000103',
        'https://cdn.techmarket.local/tenants/nexo-redes/logo.png',
        'https://cdn.techmarket.local/tenants/nexo-redes/cover.jpg',
        'Redes empresariales, Wi-Fi y seguridad de borde.',
        'Implementacion de redes cableadas e inalambricas para oficinas, tiendas y coworks.',
        'https://nexo-redes.example.local',
        'https://facebook.com/nexoredesseed',
        'https://instagram.com/nexoredesseed',
        4.60,
        27
    )
ON CONFLICT (id) DO UPDATE SET
    logo_url = EXCLUDED.logo_url,
    cover_image_url = EXCLUDED.cover_image_url,
    short_description = EXCLUDED.short_description,
    full_description = EXCLUDED.full_description,
    website_url = EXCLUDED.website_url,
    facebook_url = EXCLUDED.facebook_url,
    instagram_url = EXCLUDED.instagram_url,
    rating_average = EXCLUDED.rating_average,
    reviews_count = EXCLUDED.reviews_count;

INSERT INTO branches (
    id,
    tenant_id,
    name,
    address,
    city,
    latitude,
    longitude,
    phone,
    opening_hours,
    is_main_branch,
    status,
    created_at
) VALUES
    (
        '52000000-0000-0000-0000-000000000101',
        '50000000-0000-0000-0000-000000000101',
        'Sucursal Central Andes',
        'Av. Arce 2450, Sopocachi',
        'La Paz',
        -16.509240,
        -68.122719,
        '+59170002001',
        'Lun-Vie 09:00-18:30; Sab 09:00-13:00',
        TRUE,
        'ACTIVE',
        NOW() - INTERVAL '90 days'
    ),
    (
        '52000000-0000-0000-0000-000000000102',
        '50000000-0000-0000-0000-000000000102',
        'Laboratorio ByteLab',
        'Calle 21 de Calacoto 8420',
        'La Paz',
        -16.539091,
        -68.079017,
        '+59170002002',
        'Lun-Sab 08:30-19:00',
        TRUE,
        'ACTIVE',
        NOW() - INTERVAL '75 days'
    ),
    (
        '52000000-0000-0000-0000-000000000103',
        '50000000-0000-0000-0000-000000000103',
        'Oficina Nexo Redes',
        'Av. Banzer 6to Anillo',
        'Santa Cruz',
        -17.736092,
        -63.174406,
        '+59170002003',
        'Lun-Vie 08:30-18:00',
        TRUE,
        'ACTIVE',
        NOW() - INTERVAL '60 days'
    )
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    address = EXCLUDED.address,
    city = EXCLUDED.city,
    latitude = EXCLUDED.latitude,
    longitude = EXCLUDED.longitude,
    phone = EXCLUDED.phone,
    opening_hours = EXCLUDED.opening_hours,
    is_main_branch = EXCLUDED.is_main_branch,
    status = EXCLUDED.status;

INSERT INTO listings (
    id,
    tenant_id,
    category_id,
    brand_id,
    listing_type,
    title,
    description,
    base_price,
    currency,
    status,
    is_visible,
    internal_sku,
    created_at,
    updated_at
) VALUES
    (
        '60000000-0000-0000-0000-000000000101',
        '50000000-0000-0000-0000-000000000101',
        '30000000-0000-0000-0000-000000000101',
        '40000000-0000-0000-0000-000000000101',
        'PRODUCT',
        'Lenovo ThinkPad E14 Ryzen 7',
        'Laptop empresarial de 14 pulgadas con 16 GB RAM, SSD 512 GB y Windows 11 Pro.',
        7350.00,
        'BOB',
        'ACTIVE',
        TRUE,
        'ATS-LEN-E14-R7',
        NOW() - INTERVAL '30 days',
        NOW()
    ),
    (
        '60000000-0000-0000-0000-000000000102',
        '50000000-0000-0000-0000-000000000101',
        '30000000-0000-0000-0000-000000000102',
        '40000000-0000-0000-0000-000000000105',
        'PRODUCT',
        'Kingston NV2 SSD 1TB NVMe',
        'Unidad de estado solido M.2 NVMe PCIe para upgrades de laptops y PCs.',
        620.00,
        'BOB',
        'ACTIVE',
        TRUE,
        'ATS-KIN-NV2-1TB',
        NOW() - INTERVAL '24 days',
        NOW()
    ),
    (
        '60000000-0000-0000-0000-000000000103',
        '50000000-0000-0000-0000-000000000101',
        '30000000-0000-0000-0000-000000000103',
        '40000000-0000-0000-0000-000000000103',
        'PRODUCT',
        'Logitech MX Keys Mini',
        'Teclado compacto inalambrico para productividad, compatible con Windows, macOS y Linux.',
        890.00,
        'BOB',
        'ACTIVE',
        TRUE,
        'ATS-LOG-MXKEYS-MINI',
        NOW() - INTERVAL '18 days',
        NOW()
    ),
    (
        '60000000-0000-0000-0000-000000000104',
        '50000000-0000-0000-0000-000000000103',
        '30000000-0000-0000-0000-000000000104',
        '40000000-0000-0000-0000-000000000104',
        'PRODUCT',
        'TP-Link Deco X50 Wi-Fi 6',
        'Sistema mesh Wi-Fi 6 de dos nodos para hogares, oficinas pequenas y coworks.',
        1580.00,
        'BOB',
        'ACTIVE',
        TRUE,
        'NRB-TPL-DECO-X50',
        NOW() - INTERVAL '16 days',
        NOW()
    ),
    (
        '60000000-0000-0000-0000-000000000105',
        '50000000-0000-0000-0000-000000000102',
        '30000000-0000-0000-0000-000000000106',
        NULL,
        'SERVICE',
        'Mantenimiento preventivo de laptop',
        'Limpieza interna, cambio de pasta termica, revision de disco, memoria y temperatura.',
        180.00,
        'BOB',
        'ACTIVE',
        TRUE,
        'BYT-SRV-MANT-LAP',
        NOW() - INTERVAL '12 days',
        NOW()
    ),
    (
        '60000000-0000-0000-0000-000000000106',
        '50000000-0000-0000-0000-000000000103',
        '30000000-0000-0000-0000-000000000107',
        NULL,
        'SERVICE',
        'Instalacion de red Wi-Fi empresarial',
        'Diseno, instalacion y configuracion de access points para oficinas de hasta 30 usuarios.',
        1450.00,
        'BOB',
        'ACTIVE',
        TRUE,
        'NRB-SRV-WIFI-EMP',
        NOW() - INTERVAL '10 days',
        NOW()
    )
ON CONFLICT (id) DO UPDATE SET
    tenant_id = EXCLUDED.tenant_id,
    category_id = EXCLUDED.category_id,
    brand_id = EXCLUDED.brand_id,
    listing_type = EXCLUDED.listing_type,
    title = EXCLUDED.title,
    description = EXCLUDED.description,
    base_price = EXCLUDED.base_price,
    currency = EXCLUDED.currency,
    status = EXCLUDED.status,
    is_visible = EXCLUDED.is_visible,
    internal_sku = EXCLUDED.internal_sku,
    updated_at = NOW();

INSERT INTO branch_listings (id, branch_id, listing_id, status, is_available, created_at) VALUES
    ('61000000-0000-0000-0000-000000000101', '52000000-0000-0000-0000-000000000101', '60000000-0000-0000-0000-000000000101', 'ACTIVE', TRUE, NOW()),
    ('61000000-0000-0000-0000-000000000102', '52000000-0000-0000-0000-000000000101', '60000000-0000-0000-0000-000000000102', 'ACTIVE', TRUE, NOW()),
    ('61000000-0000-0000-0000-000000000103', '52000000-0000-0000-0000-000000000101', '60000000-0000-0000-0000-000000000103', 'ACTIVE', TRUE, NOW()),
    ('61000000-0000-0000-0000-000000000104', '52000000-0000-0000-0000-000000000103', '60000000-0000-0000-0000-000000000104', 'ACTIVE', TRUE, NOW()),
    ('61000000-0000-0000-0000-000000000105', '52000000-0000-0000-0000-000000000102', '60000000-0000-0000-0000-000000000105', 'ACTIVE', TRUE, NOW()),
    ('61000000-0000-0000-0000-000000000106', '52000000-0000-0000-0000-000000000103', '60000000-0000-0000-0000-000000000106', 'ACTIVE', TRUE, NOW())
ON CONFLICT (id) DO UPDATE SET
    branch_id = EXCLUDED.branch_id,
    listing_id = EXCLUDED.listing_id,
    status = EXCLUDED.status,
    is_available = EXCLUDED.is_available;

INSERT INTO listing_images (id, listing_id, image_url, display_order, is_primary) VALUES
    ('62000000-0000-0000-0000-000000000101', '60000000-0000-0000-0000-000000000101', 'https://cdn.techmarket.local/listings/thinkpad-e14/main.jpg', '1', TRUE),
    ('62000000-0000-0000-0000-000000000102', '60000000-0000-0000-0000-000000000102', 'https://cdn.techmarket.local/listings/kingston-nv2/main.jpg', '1', TRUE),
    ('62000000-0000-0000-0000-000000000103', '60000000-0000-0000-0000-000000000103', 'https://cdn.techmarket.local/listings/mx-keys-mini/main.jpg', '1', TRUE),
    ('62000000-0000-0000-0000-000000000104', '60000000-0000-0000-0000-000000000104', 'https://cdn.techmarket.local/listings/deco-x50/main.jpg', '1', TRUE),
    ('62000000-0000-0000-0000-000000000105', '60000000-0000-0000-0000-000000000105', 'https://cdn.techmarket.local/listings/mantenimiento-laptop/main.jpg', '1', TRUE),
    ('62000000-0000-0000-0000-000000000106', '60000000-0000-0000-0000-000000000106', 'https://cdn.techmarket.local/listings/wifi-empresarial/main.jpg', '1', TRUE)
ON CONFLICT (id) DO UPDATE SET
    image_url = EXCLUDED.image_url,
    display_order = EXCLUDED.display_order,
    is_primary = EXCLUDED.is_primary;

INSERT INTO listing_specifications (
    id,
    listing_id,
    attribute_name,
    attribute_value,
    unit,
    is_normalized
) VALUES
    ('63000000-0000-0000-0000-000000000101', '60000000-0000-0000-0000-000000000101', 'Memoria RAM', '16', 'GB', TRUE),
    ('63000000-0000-0000-0000-000000000102', '60000000-0000-0000-0000-000000000101', 'Almacenamiento', '512', 'GB SSD', TRUE),
    ('63000000-0000-0000-0000-000000000103', '60000000-0000-0000-0000-000000000102', 'Capacidad', '1', 'TB', TRUE),
    ('63000000-0000-0000-0000-000000000104', '60000000-0000-0000-0000-000000000103', 'Conectividad', 'Bluetooth', NULL, TRUE),
    ('63000000-0000-0000-0000-000000000105', '60000000-0000-0000-0000-000000000104', 'Estandar Wi-Fi', 'Wi-Fi 6', NULL, TRUE)
ON CONFLICT (id) DO UPDATE SET
    attribute_name = EXCLUDED.attribute_name,
    attribute_value = EXCLUDED.attribute_value,
    unit = EXCLUDED.unit,
    is_normalized = EXCLUDED.is_normalized;

INSERT INTO branch_inventory (
    id,
    listing_id,
    branch_id,
    stock_available,
    stock_reserved,
    minimum_stock,
    updated_at
) VALUES
    ('64000000-0000-0000-0000-000000000101', '60000000-0000-0000-0000-000000000101', '52000000-0000-0000-0000-000000000101', '8', '1', '2', NOW()),
    ('64000000-0000-0000-0000-000000000102', '60000000-0000-0000-0000-000000000102', '52000000-0000-0000-0000-000000000101', '24', '3', '5', NOW()),
    ('64000000-0000-0000-0000-000000000103', '60000000-0000-0000-0000-000000000103', '52000000-0000-0000-0000-000000000101', '12', '2', '3', NOW()),
    ('64000000-0000-0000-0000-000000000104', '60000000-0000-0000-0000-000000000104', '52000000-0000-0000-0000-000000000103', '10', '1', '2', NOW())
ON CONFLICT (id) DO UPDATE SET
    stock_available = EXCLUDED.stock_available,
    stock_reserved = EXCLUDED.stock_reserved,
    minimum_stock = EXCLUDED.minimum_stock,
    updated_at = NOW();

INSERT INTO service_details (
    id,
    listing_id,
    estimated_duration_minutes,
    requires_diagnosis,
    offers_on_site_service,
    service_area,
    terms_and_conditions
) VALUES
    (
        '65000000-0000-0000-0000-000000000101',
        '60000000-0000-0000-0000-000000000105',
        120,
        'false',
        'true',
        'La Paz y El Alto',
        'Incluye limpieza y diagnostico basico; repuestos se cotizan por separado.'
    ),
    (
        '65000000-0000-0000-0000-000000000102',
        '60000000-0000-0000-0000-000000000106',
        480,
        'true',
        'true',
        'Santa Cruz',
        'Precio base para instalacion estandar; cableado y equipos adicionales se cotizan.'
    )
ON CONFLICT (id) DO UPDATE SET
    estimated_duration_minutes = EXCLUDED.estimated_duration_minutes,
    requires_diagnosis = EXCLUDED.requires_diagnosis,
    offers_on_site_service = EXCLUDED.offers_on_site_service,
    service_area = EXCLUDED.service_area,
    terms_and_conditions = EXCLUDED.terms_and_conditions;

INSERT INTO specialist_profiles (
    id,
    user_id,
    specialty,
    location,
    photo_url,
    created_at,
    updated_at
) VALUES
    (
        '70000000-0000-0000-0000-000000000101',
        '10000000-0000-0000-0000-000000000103',
        'Reparacion de laptops y recuperacion de rendimiento',
        'La Paz',
        'https://cdn.techmarket.local/specialists/camila-quiroga.jpg',
        NOW() - INTERVAL '40 days',
        NOW()
    ),
    (
        '70000000-0000-0000-0000-000000000102',
        '10000000-0000-0000-0000-000000000104',
        'Redes, cableado estructurado y Wi-Fi empresarial',
        'Santa Cruz',
        'https://cdn.techmarket.local/specialists/marco-salvatierra.jpg',
        NOW() - INTERVAL '35 days',
        NOW()
    )
ON CONFLICT (id) DO UPDATE SET
    specialty = EXCLUDED.specialty,
    location = EXCLUDED.location,
    photo_url = EXCLUDED.photo_url,
    updated_at = NOW();

INSERT INTO specialist_services (
    id,
    user_id,
    name,
    description,
    price,
    currency,
    service_type,
    featured,
    created_at,
    updated_at
) VALUES
    (
        '71000000-0000-0000-0000-000000000101',
        '10000000-0000-0000-0000-000000000103',
        'Upgrade de SSD y memoria RAM',
        'Instalacion de SSD, migracion de sistema y ampliacion de memoria para laptops compatibles.',
        220.00,
        'BOB',
        'UPGRADE',
        TRUE,
        NOW() - INTERVAL '20 days',
        NOW()
    ),
    (
        '71000000-0000-0000-0000-000000000102',
        '10000000-0000-0000-0000-000000000103',
        'Diagnostico avanzado de laptop',
        'Revision de placa, almacenamiento, temperaturas, bateria y reporte tecnico documentado.',
        120.00,
        'BOB',
        'DIAGNOSTIC',
        FALSE,
        NOW() - INTERVAL '18 days',
        NOW()
    ),
    (
        '71000000-0000-0000-0000-000000000103',
        '10000000-0000-0000-0000-000000000104',
        'Auditoria de red Wi-Fi',
        'Mapa de cobertura, deteccion de interferencias y recomendaciones de ubicacion de equipos.',
        350.00,
        'BOB',
        'NETWORKING',
        TRUE,
        NOW() - INTERVAL '16 days',
        NOW()
    ),
    (
        '71000000-0000-0000-0000-000000000104',
        '10000000-0000-0000-0000-000000000104',
        'Configuracion de firewall para pyme',
        'Reglas base de seguridad, segmentacion inicial y documentacion de acceso administrativo.',
        520.00,
        'BOB',
        'SECURITY',
        FALSE,
        NOW() - INTERVAL '14 days',
        NOW()
    )
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    price = EXCLUDED.price,
    currency = EXCLUDED.currency,
    service_type = EXCLUDED.service_type,
    featured = EXCLUDED.featured,
    updated_at = NOW();

INSERT INTO communities (id, name, description, members_count, created_at) VALUES
    (
        '80000000-0000-0000-0000-000000000101',
        'Armadores PC Bolivia',
        'Comunidad para comparar componentes, compartir builds y resolver dudas de compatibilidad.',
        2,
        NOW() - INTERVAL '45 days'
    ),
    (
        '80000000-0000-0000-0000-000000000102',
        'Soporte Tecnico TechMarket',
        'Espacio para buenas practicas de mantenimiento, diagnostico y reparacion de equipos.',
        2,
        NOW() - INTERVAL '38 days'
    ),
    (
        '80000000-0000-0000-0000-000000000103',
        'Redes para Pymes',
        'Charlas y recomendaciones sobre Wi-Fi, cableado, routers, seguridad y monitoreo.',
        1,
        NOW() - INTERVAL '31 days'
    )
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    members_count = EXCLUDED.members_count;

INSERT INTO community_memberships (id, community_id, user_id, joined_at) VALUES
    ('81000000-0000-0000-0000-000000000101', '80000000-0000-0000-0000-000000000101', '10000000-0000-0000-0000-000000000101', NOW() - INTERVAL '44 days'),
    ('81000000-0000-0000-0000-000000000102', '80000000-0000-0000-0000-000000000101', '10000000-0000-0000-0000-000000000102', NOW() - INTERVAL '43 days'),
    ('81000000-0000-0000-0000-000000000103', '80000000-0000-0000-0000-000000000102', '10000000-0000-0000-0000-000000000102', NOW() - INTERVAL '37 days'),
    ('81000000-0000-0000-0000-000000000104', '80000000-0000-0000-0000-000000000102', '10000000-0000-0000-0000-000000000103', NOW() - INTERVAL '36 days'),
    ('81000000-0000-0000-0000-000000000105', '80000000-0000-0000-0000-000000000103', '10000000-0000-0000-0000-000000000104', NOW() - INTERVAL '30 days')
ON CONFLICT (id) DO UPDATE SET
    community_id = EXCLUDED.community_id,
    user_id = EXCLUDED.user_id,
    joined_at = EXCLUDED.joined_at;

INSERT INTO feed_posts (
    id,
    tenant_id,
    author_user_id,
    post_type,
    title,
    content,
    status,
    created_at,
    community_id
) VALUES
    (
        '82000000-0000-0000-0000-000000000101',
        NULL,
        '10000000-0000-0000-0000-000000000101',
        'COMMUNITY',
        'Build equilibrada para desarrollo',
        'Comparti una configuracion Ryzen 7 con 32 GB RAM que funciona bien para Docker y Java.',
        'PUBLISHED',
        NOW() - INTERVAL '20 days',
        '80000000-0000-0000-0000-000000000101'
    ),
    (
        '82000000-0000-0000-0000-000000000102',
        NULL,
        '10000000-0000-0000-0000-000000000103',
        'COMMUNITY',
        'Senales de mantenimiento urgente',
        'Temperaturas altas, apagados repentinos y ruido del ventilador suelen indicar limpieza pendiente.',
        'PUBLISHED',
        NOW() - INTERVAL '15 days',
        '80000000-0000-0000-0000-000000000102'
    ),
    (
        '82000000-0000-0000-0000-000000000103',
        NULL,
        '10000000-0000-0000-0000-000000000104',
        'COMMUNITY',
        'Checklist Wi-Fi para oficinas pequenas',
        'Antes de comprar repetidores conviene medir cobertura, interferencia y cantidad real de usuarios.',
        'PUBLISHED',
        NOW() - INTERVAL '9 days',
        '80000000-0000-0000-0000-000000000103'
    )
ON CONFLICT (id) DO UPDATE SET
    tenant_id = EXCLUDED.tenant_id,
    author_user_id = EXCLUDED.author_user_id,
    post_type = EXCLUDED.post_type,
    title = EXCLUDED.title,
    content = EXCLUDED.content,
    status = EXCLUDED.status,
    community_id = EXCLUDED.community_id;
