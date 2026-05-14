CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- =============================================================================
-- Seed: cliente.test@techmarket.com / Cliente123!
-- Sofia Flores — usuario cliente de prueba
-- UUID del usuario se pasa como variable psql: -v client_uuid=<UUID>
-- El UUID se calcula del ID IAM: 00000000-0000-0000-0000-<IAM_ID_padded>
-- =============================================================================

BEGIN;

-- ─── USUARIO ──────────────────────────────────────────────────────────────────
INSERT INTO users (id, first_name, last_name, email, phone, password_hash, status, last_login_at, created_at, updated_at)
VALUES (
    :'client_uuid',
    'Sofia',
    'Flores',
    'cliente.test@techmarket.com',
    '+59171234567',
    crypt('Cliente123!', gen_salt('bf', 10)),
    'ACTIVE',
    NULL,
    NOW() - INTERVAL '10 days',
    NOW()
)
ON CONFLICT (id) DO UPDATE SET
    first_name = EXCLUDED.first_name,
    last_name  = EXCLUDED.last_name,
    phone      = EXCLUDED.phone,
    status     = EXCLUDED.status,
    updated_at = NOW();

-- ─── DIRECCIONES ──────────────────────────────────────────────────────────────
INSERT INTO client_addresses (id, user_id, title, country, city, address, reference, default_address, created_at, updated_at)
VALUES
    (
        'c11e0100-feed-4001-a000-000000000001',
        :'client_uuid',
        'Casa',
        'Bolivia',
        'La Paz',
        'Av. 6 de Agosto 2345, Sopocachi',
        'Edificio azul, piso 3, dpto 301',
        TRUE,
        NOW() - INTERVAL '9 days',
        NOW()
    ),
    (
        'c11e0100-feed-4001-a000-000000000002',
        :'client_uuid',
        'Trabajo',
        'Bolivia',
        'La Paz',
        'Calle Loayza 233, Centro',
        'Edificio Torre Ejecutiva, piso 5',
        FALSE,
        NOW() - INTERVAL '7 days',
        NOW()
    )
ON CONFLICT (id) DO UPDATE SET
    title           = EXCLUDED.title,
    address         = EXCLUDED.address,
    reference       = EXCLUDED.reference,
    default_address = EXCLUDED.default_address,
    updated_at      = NOW();

-- ─── CARRITO ACTUAL ───────────────────────────────────────────────────────────
INSERT INTO client_cart_items (id, user_id, listing_id, quantity, unit_price, created_at, updated_at)
VALUES
    (
        'c11e0200-feed-4001-a000-000000000001',
        :'client_uuid',
        '60000000-0000-0000-0000-000000000103',
        1,
        890.00,
        NOW() - INTERVAL '1 day',
        NOW()
    ),
    (
        'c11e0200-feed-4001-a000-000000000002',
        :'client_uuid',
        '60000000-0000-0000-0000-000000000102',
        2,
        620.00,
        NOW() - INTERVAL '1 day',
        NOW()
    )
ON CONFLICT (id) DO UPDATE SET
    quantity   = EXCLUDED.quantity,
    unit_price = EXCLUDED.unit_price,
    updated_at = NOW();

-- ─── PEDIDOS ──────────────────────────────────────────────────────────────────
INSERT INTO client_orders (id, user_id, shipping_address_id, payment_method, status, total, created_at, updated_at)
VALUES
    (
        'c11e0300-feed-4001-a000-000000000001',
        :'client_uuid',
        'c11e0100-feed-4001-a000-000000000001',
        'QR',
        'DELIVERED',
        7350.00,
        NOW() - INTERVAL '25 days',
        NOW() - INTERVAL '20 days'
    ),
    (
        'c11e0300-feed-4001-a000-000000000002',
        :'client_uuid',
        'c11e0100-feed-4001-a000-000000000001',
        'EFECTIVO',
        'PENDING',
        2200.00,
        NOW() - INTERVAL '3 days',
        NOW() - INTERVAL '3 days'
    )
ON CONFLICT (id) DO UPDATE SET
    status     = EXCLUDED.status,
    total      = EXCLUDED.total,
    updated_at = NOW();

-- ─── ITEMS DE PEDIDOS ─────────────────────────────────────────────────────────
INSERT INTO client_order_items (id, order_id, listing_id, quantity, unit_price)
VALUES
    (
        'c11e0400-feed-4001-a000-000000000001',
        'c11e0300-feed-4001-a000-000000000001',
        '60000000-0000-0000-0000-000000000101',
        1,
        7350.00
    ),
    (
        'c11e0400-feed-4001-a000-000000000002',
        'c11e0300-feed-4001-a000-000000000002',
        '60000000-0000-0000-0000-000000000104',
        1,
        1580.00
    ),
    (
        'c11e0400-feed-4001-a000-000000000003',
        'c11e0300-feed-4001-a000-000000000002',
        '60000000-0000-0000-0000-000000000102',
        1,
        620.00
    )
ON CONFLICT (id) DO UPDATE SET
    quantity   = EXCLUDED.quantity,
    unit_price = EXCLUDED.unit_price;

-- ─── RESEÑAS ──────────────────────────────────────────────────────────────────
INSERT INTO reviews (id, tenant_id, user_id, ticket_id, quote_id, rating, comment, moderation_status, created_at, listing_id, updated_at)
VALUES
    (
        'c11e0500-feed-4001-a000-000000000001',
        '50000000-0000-0000-0000-000000000101',
        :'client_uuid',
        NULL,
        NULL,
        5.00,
        'Excelente laptop, muy rapida y la bateria dura todo el dia. Llego en perfectas condiciones.',
        'APPROVED',
        NOW() - INTERVAL '18 days',
        '60000000-0000-0000-0000-000000000101',
        NOW() - INTERVAL '18 days'
    ),
    (
        'c11e0500-feed-4001-a000-000000000002',
        '50000000-0000-0000-0000-000000000102',
        :'client_uuid',
        NULL,
        NULL,
        4.00,
        'Muy buen servicio de mantenimiento. El equipo quedo como nuevo. Recomendado.',
        'APPROVED',
        NOW() - INTERVAL '10 days',
        '60000000-0000-0000-0000-000000000105',
        NOW() - INTERVAL '10 days'
    )
ON CONFLICT (id) DO UPDATE SET
    rating            = EXCLUDED.rating,
    comment           = EXCLUDED.comment,
    moderation_status = EXCLUDED.moderation_status,
    updated_at        = NOW();

-- ─── CHAT / SOPORTE ───────────────────────────────────────────────────────────
INSERT INTO tickets (id, ticket_code, tenant_id, branch_id, customer_user_id, lead_id, listing_id, assigned_user_id, assigned_technician_user_id, ticket_type, subject, description, priority, status, opened_at, closed_at, created_at)
VALUES (
    'c11e0600-feed-4001-a000-000000000001',
    'CHT-CLI-0001',
    '50000000-0000-0000-0000-000000000102',
    '52000000-0000-0000-0000-000000000102',
    :'client_uuid',
    NULL,
    '60000000-0000-0000-0000-000000000105',
    NULL,
    NULL,
    'CHAT',
    'Consulta sobre servicio de mantenimiento preventivo',
    'Quisiera saber si el servicio incluye cambio de pasta termica y cuanto demora.',
    'MEDIUM',
    'OPEN',
    NOW() - INTERVAL '5 days',
    NULL,
    NOW() - INTERVAL '5 days'
)
ON CONFLICT (id) DO UPDATE SET
    subject     = EXCLUDED.subject,
    description = EXCLUDED.description,
    status      = EXCLUDED.status;

INSERT INTO ticket_messages (id, ticket_id, author_user_id, message_body, message_type, is_visible_to_customer, created_at)
VALUES
    (
        'c11e0700-feed-4001-a000-000000000001',
        'c11e0600-feed-4001-a000-000000000001',
        :'client_uuid',
        'Hola! Queria consultar si el servicio de mantenimiento incluye el cambio de pasta termica o es aparte.',
        'TEXT',
        TRUE,
        NOW() - INTERVAL '5 days'
    ),
    (
        'c11e0700-feed-4001-a000-000000000002',
        'c11e0600-feed-4001-a000-000000000001',
        '10000000-0000-0000-0000-000000000103',
        'Hola Sofia! Si, el servicio estandar incluye limpieza interna y cambio de pasta termica. El tiempo estimado es de 2 horas.',
        'TEXT',
        TRUE,
        NOW() - INTERVAL '4 days' + INTERVAL '3 hours'
    ),
    (
        'c11e0700-feed-4001-a000-000000000003',
        'c11e0600-feed-4001-a000-000000000001',
        :'client_uuid',
        'Perfecto, muchas gracias! Lo agendare para la proxima semana.',
        'TEXT',
        TRUE,
        NOW() - INTERVAL '4 days' + INTERVAL '4 hours'
    )
ON CONFLICT (id) DO UPDATE SET
    message_body = EXCLUDED.message_body;

INSERT INTO chat_read_receipts (id, ticket_id, user_id, read_at)
VALUES (
    'c11e0800-feed-4001-a000-000000000001',
    'c11e0600-feed-4001-a000-000000000001',
    :'client_uuid',
    NOW() - INTERVAL '4 days' + INTERVAL '5 hours'
)
ON CONFLICT (ticket_id, user_id) DO UPDATE SET
    read_at = EXCLUDED.read_at;

-- ─── FAVORITOS ────────────────────────────────────────────────────────────────
INSERT INTO favorites (id, user_id, tenant_id, listing_id, created_at)
VALUES
    (
        'c11e0900-feed-4001-a000-000000000001',
        :'client_uuid',
        '50000000-0000-0000-0000-000000000101',
        '60000000-0000-0000-0000-000000000101',
        NOW() - INTERVAL '8 days'
    ),
    (
        'c11e0900-feed-4001-a000-000000000002',
        :'client_uuid',
        '50000000-0000-0000-0000-000000000103',
        '60000000-0000-0000-0000-000000000104',
        NOW() - INTERVAL '6 days'
    ),
    (
        'c11e0900-feed-4001-a000-000000000003',
        :'client_uuid',
        '50000000-0000-0000-0000-000000000102',
        NULL,
        NOW() - INTERVAL '4 days'
    )
ON CONFLICT (id) DO UPDATE SET
    listing_id = EXCLUDED.listing_id,
    tenant_id  = EXCLUDED.tenant_id;

-- ─── COMUNIDADES ──────────────────────────────────────────────────────────────
INSERT INTO community_memberships (id, community_id, user_id, joined_at)
VALUES
    (
        'c11e0a00-feed-4001-a000-000000000001',
        '80000000-0000-0000-0000-000000000101',
        :'client_uuid',
        NOW() - INTERVAL '7 days'
    ),
    (
        'c11e0a00-feed-4001-a000-000000000002',
        '80000000-0000-0000-0000-000000000102',
        :'client_uuid',
        NOW() - INTERVAL '5 days'
    )
ON CONFLICT (community_id, user_id) DO UPDATE SET
    joined_at = EXCLUDED.joined_at;

UPDATE communities
SET members_count = (SELECT COUNT(*) FROM community_memberships WHERE community_id = communities.id)
WHERE id IN (
    '80000000-0000-0000-0000-000000000101',
    '80000000-0000-0000-0000-000000000102'
);

-- ─── PUBLICACIONES EN COMUNIDADES ────────────────────────────────────────────
INSERT INTO feed_posts (id, tenant_id, author_user_id, post_type, title, content, status, created_at, community_id)
VALUES
    (
        'c11e0c00-feed-4001-a000-000000000001',
        NULL,
        :'client_uuid',
        'COMMUNITY',
        'Mi experiencia con el ThinkPad E14 para trabajo remoto',
        'Recien la compre en Andes Tech Store y cumple todo lo que necesitaba para home office. Bateria de 8 horas, SSD rapido y sin problemas corriendo Docker, IntelliJ y Chrome juntos. Muy recomendada.',
        'PUBLISHED',
        NOW() - INTERVAL '6 days',
        '80000000-0000-0000-0000-000000000101'
    ),
    (
        'c11e0c00-feed-4001-a000-000000000002',
        NULL,
        :'client_uuid',
        'COMMUNITY',
        'Mantenimiento preventivo en ByteLab: vale la pena',
        'Le hice el mantenimiento preventivo despues de 2 anios sin limpiarla. El cambio de pasta termica fue clave: bajo de 92 a 65 grados en carga. El equipo responde mucho mejor y el ventilador ya no molesta.',
        'PUBLISHED',
        NOW() - INTERVAL '9 days',
        '80000000-0000-0000-0000-000000000102'
    )
ON CONFLICT (id) DO UPDATE SET
    title   = EXCLUDED.title,
    content = EXCLUDED.content,
    status  = EXCLUDED.status;

-- ─── NOTIFICACIONES ───────────────────────────────────────────────────────────
INSERT INTO notifications (id, user_id, notification_type, title, message, channel, is_read, sent_at, link_url)
VALUES
    (
        'c11e0b00-feed-4001-a000-000000000001',
        :'client_uuid',
        'ORDER_CONFIRMED',
        'Pedido confirmado',
        'Tu pedido del Lenovo ThinkPad E14 fue confirmado y esta en preparacion.',
        'IN_APP',
        TRUE,
        NOW() - INTERVAL '25 days',
        '/cliente/pedidos/c11e0300-feed-4001-a000-000000000001'
    ),
    (
        'c11e0b00-feed-4001-a000-000000000002',
        :'client_uuid',
        'ORDER_DELIVERED',
        'Pedido entregado',
        'Tu pedido del Lenovo ThinkPad E14 fue entregado. Deja tu resena!',
        'IN_APP',
        TRUE,
        NOW() - INTERVAL '20 days',
        '/cliente/pedidos/c11e0300-feed-4001-a000-000000000001'
    ),
    (
        'c11e0b00-feed-4001-a000-000000000003',
        :'client_uuid',
        'CHAT_MESSAGE',
        'Nueva respuesta en tu consulta',
        'ByteLab Servicios respondio tu consulta sobre mantenimiento preventivo.',
        'IN_APP',
        FALSE,
        NOW() - INTERVAL '4 days' + INTERVAL '3 hours',
        '/cliente/chats/c11e0600-feed-4001-a000-000000000001'
    )
ON CONFLICT (id) DO UPDATE SET
    title   = EXCLUDED.title,
    message = EXCLUDED.message,
    is_read = EXCLUDED.is_read;

COMMIT;
