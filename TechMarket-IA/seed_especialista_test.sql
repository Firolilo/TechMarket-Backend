-- =============================================================================
-- SEEDER DE PRUEBA: Especialista en TechMarket-IA (vertical)
-- Base de datos: TechMarket-IA (microservicio principal)
--
-- IMPORTANTE: ejecutar DESPUÉS de seed_especialista_test.sql en IAM.
-- El email debe ser idéntico al creado en IAM para que la integración funcione.
-- Email:    especialista.test@techmarket.com
-- Password: Especialista123!
-- =============================================================================

CREATE EXTENSION IF NOT EXISTS pgcrypto;

BEGIN;

DO $$
DECLARE
    -- UUIDs fijos para reproducibilidad en pruebas
    v_user_id         UUID := 'facade00-feed-4000-a000-000000000001';
    v_profile_id      UUID := 'facade00-feed-4000-a000-000000000002';
    v_availability_id UUID := 'facade00-feed-4000-a000-000000000003';
    v_service1_id     UUID := 'facade00-feed-4000-a000-000000000010';
    v_service2_id     UUID := 'facade00-feed-4000-a000-000000000011';
    v_service3_id     UUID := 'facade00-feed-4000-a000-000000000012';
    v_portfolio1_id   UUID := 'facade00-feed-4000-a000-000000000020';
    v_portfolio2_id   UUID := 'facade00-feed-4000-a000-000000000021';
    v_cert1_id        UUID := 'facade00-feed-4000-a000-000000000030';
    v_cert2_id        UUID := 'facade00-feed-4000-a000-000000000031';
    v_tx1_id          UUID := 'facade00-feed-4000-a000-000000000040';
    v_tx2_id          UUID := 'facade00-feed-4000-a000-000000000041';
    v_withdrawal1_id  UUID := 'facade00-feed-4000-a000-000000000050';
    v_user_role_id    UUID := 'facade00-feed-4000-a000-000000000060';
    v_role_id         UUID;
    v_real_user_id    UUID;
BEGIN

    -- =========================================================================
    -- 1. Rol especialista en tabla `roles` (si no existe)
    -- =========================================================================
    INSERT INTO roles (id, name, description)
    SELECT gen_random_uuid(), 'especialista', 'Especialista técnico de TechMarket'
    WHERE NOT EXISTS (SELECT 1 FROM roles WHERE LOWER(name) = 'especialista');

    SELECT id INTO v_role_id FROM roles WHERE LOWER(name) = 'especialista';

    -- =========================================================================
    -- 2. Usuario en tabla `users` (dominio IA)
    -- El email ES el vínculo entre IAM y el dominio.
    -- =========================================================================
    INSERT INTO users (
        id, first_name, last_name, email, phone,
        password_hash, status, created_at, updated_at
    )
    SELECT
        v_user_id,
        'Carlos',
        'Techero',
        'especialista.test@techmarket.com',
        '+591 70123456',
        crypt('Especialista123!', gen_salt('bf', 10)),
        'active',
        NOW(),
        NOW()
    WHERE NOT EXISTS (
        SELECT 1 FROM users
        WHERE LOWER(email) = LOWER('especialista.test@techmarket.com')
    );

    -- Recuperar el ID real (por si el usuario ya existía con otro UUID)
    SELECT id INTO v_real_user_id
    FROM users
    WHERE LOWER(email) = LOWER('especialista.test@techmarket.com');

    -- =========================================================================
    -- 3. Asignar rol
    -- =========================================================================
    INSERT INTO user_roles (id, user_id, role_id, is_active, assigned_at)
    SELECT v_user_role_id, v_real_user_id, v_role_id, TRUE, NOW()
    WHERE NOT EXISTS (
        SELECT 1 FROM user_roles
        WHERE user_id = v_real_user_id AND role_id = v_role_id
    );

    -- =========================================================================
    -- 4. Perfil de especialista
    -- =========================================================================
    INSERT INTO specialist_profiles (
        id, user_id, specialty, location, photo_url, created_at, updated_at
    )
    VALUES (
        v_profile_id,
        v_real_user_id,
        'Reparación y mantenimiento de computadoras',
        'La Paz, Bolivia',
        NULL,
        NOW(),
        NOW()
    )
    ON CONFLICT ON CONSTRAINT uk_specialist_profiles_user_id DO NOTHING;

    -- =========================================================================
    -- 5. Servicios ofrecidos
    -- =========================================================================
    INSERT INTO specialist_services (
        id, user_id, name, description, price, currency, service_type, featured, created_at, updated_at
    )
    SELECT * FROM (VALUES
        (v_service1_id, v_real_user_id,
         'Reparación de laptops',
         'Diagnóstico y reparación completa de laptops y notebooks',
         150.00, 'Bs', 'HARDWARE', TRUE, NOW(), NOW()),
        (v_service2_id, v_real_user_id,
         'Formateo e instalación de software',
         'Formateo con instalación de Windows y programas esenciales',
         80.00, 'Bs', 'SOFTWARE', FALSE, NOW(), NOW()),
        (v_service3_id, v_real_user_id,
         'Configuración de redes',
         'Configuración de routers y redes domésticas o empresariales',
         120.00, 'Bs', 'REDES', TRUE, NOW(), NOW())
    ) AS t(id, user_id, name, description, price, currency, service_type, featured, created_at, updated_at)
    WHERE NOT EXISTS (
        SELECT 1 FROM specialist_services WHERE user_id = v_real_user_id
    );

    -- =========================================================================
    -- 6. Portafolio
    -- =========================================================================
    INSERT INTO specialist_portfolio_items (
        id, user_id, title, service_name, result, work_date, created_at, updated_at
    )
    SELECT * FROM (VALUES
        (v_portfolio1_id, v_real_user_id,
         'Recuperación de datos en disco duro dañado',
         'Reparación de laptops',
         'Datos recuperados al 100%',
         '2025-03-15', NOW(), NOW()),
        (v_portfolio2_id, v_real_user_id,
         'Instalación de red empresarial para 20 equipos',
         'Configuración de redes',
         'Red configurada y estable',
         '2025-04-20', NOW(), NOW())
    ) AS t(id, user_id, title, service_name, result, work_date, created_at, updated_at)
    WHERE NOT EXISTS (
        SELECT 1 FROM specialist_portfolio_items WHERE user_id = v_real_user_id
    );

    -- =========================================================================
    -- 7. Disponibilidad
    -- =========================================================================
    INSERT INTO specialist_availability (
        id, user_id, status,
        days_json, start_time, end_time,
        modalities_json, coverage, response_time,
        created_at, updated_at
    )
    VALUES (
        v_availability_id,
        v_real_user_id,
        'disponible',
        '["lunes","martes","miercoles","jueves","viernes"]',
        '08:00',
        '18:00',
        '["presencial","remoto"]',
        'La Paz y El Alto',
        '2 horas',
        NOW(),
        NOW()
    )
    ON CONFLICT ON CONSTRAINT uk_specialist_availability_user_id DO NOTHING;

    -- =========================================================================
    -- 8. Certificaciones
    -- =========================================================================
    INSERT INTO specialist_certifications (
        id, user_id, name, institution, obtained_at, file_url, status, created_at, updated_at
    )
    SELECT * FROM (VALUES
        (v_cert1_id, v_real_user_id,
         'CompTIA A+', 'CompTIA', '2023-06', NULL, 'verificado', NOW(), NOW()),
        (v_cert2_id, v_real_user_id,
         'Cisco CCNA', 'Cisco', '2024-01', NULL, 'pendiente', NOW(), NOW())
    ) AS t(id, user_id, name, institution, obtained_at, file_url, status, created_at, updated_at)
    WHERE NOT EXISTS (
        SELECT 1 FROM specialist_certifications WHERE user_id = v_real_user_id
    );

    -- =========================================================================
    -- 9. Transacciones (historial de cobros)
    -- =========================================================================
    INSERT INTO specialist_transactions (
        id, user_id, service_appointment_id,
        service_name, client_name,
        amount, platform_commission, currency,
        status, transaction_date, created_at, updated_at
    )
    SELECT * FROM (VALUES
        (v_tx1_id, v_real_user_id, NULL::UUID,
         'Reparación de laptops', 'Juan Pérez',
         150.00, 22.50, 'Bs',
         'completado', NOW() - INTERVAL '7 days', NOW(), NOW()),
        (v_tx2_id, v_real_user_id, NULL::UUID,
         'Formateo e instalación de software', 'María García',
         80.00, 12.00, 'Bs',
         'completado', NOW() - INTERVAL '3 days', NOW(), NOW())
    ) AS t(id, user_id, service_appointment_id, service_name, client_name,
           amount, platform_commission, currency, status, transaction_date,
           created_at, updated_at)
    WHERE NOT EXISTS (
        SELECT 1 FROM specialist_transactions WHERE user_id = v_real_user_id
    );

    -- =========================================================================
    -- 10. Retiro pendiente (para probar la vista de billetera)
    -- =========================================================================
    INSERT INTO specialist_withdrawals (
        id, user_id, amount, currency, status, requested_at, estimated_at
    )
    SELECT v_withdrawal1_id, v_real_user_id, 200.00, 'Bs', 'pendiente', NOW(), (NOW() + INTERVAL '3 days')::DATE
    WHERE NOT EXISTS (
        SELECT 1 FROM specialist_withdrawals WHERE user_id = v_real_user_id
    );

END $$;

COMMIT;

-- =============================================================================
-- Verificación rápida post-seed:
-- =============================================================================
-- SELECT u.id, u.first_name, u.last_name, u.email, u.status,
--        r.name AS rol
-- FROM users u
-- JOIN user_roles ur ON ur.user_id = u.id
-- JOIN roles r       ON r.id = ur.role_id
-- WHERE LOWER(u.email) = 'especialista.test@techmarket.com';
--
-- SELECT * FROM specialist_profiles
-- WHERE user_id = (SELECT id FROM users WHERE LOWER(email) = 'especialista.test@techmarket.com');
