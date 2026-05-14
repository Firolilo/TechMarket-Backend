-- =============================================================================
-- SEEDER DE PRUEBA: Embajador en TechMarket-IA
-- Base de datos: TechMarket-IA
-- Email:         embajador.test@techmarket.com
-- Contraseña:    Embajador123!
--
-- REQUISITO: pgcrypto habilitado.
--   CREATE EXTENSION IF NOT EXISTS pgcrypto;
-- =============================================================================

CREATE EXTENSION IF NOT EXISTS pgcrypto;

BEGIN;

-- ─────────────────────────────────────────────────────────────
-- 1. USUARIO
-- ─────────────────────────────────────────────────────────────
INSERT INTO users (id, first_name, last_name, email, password_hash, status, created_at, updated_at)
SELECT
    'aa000000-0000-0000-0000-000000000001'::uuid,
    'Lucas',
    'Embajador',
    'embajador.test@techmarket.com',
    crypt('Embajador123!', gen_salt('bf', 10)),
    'ACTIVE',
    NOW() - INTERVAL '6 months',
    NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE email = 'embajador.test@techmarket.com'
);

-- ─────────────────────────────────────────────────────────────
-- 2. PERFIL DE EMBAJADOR
-- ─────────────────────────────────────────────────────────────
INSERT INTO ambassadors (
    id, user_id, referral_code, status, level, activated_at,
    country, city, description,
    email_notifications, push_notifications, public_profile, language
)
SELECT
    'aa000000-0000-0000-0000-000000000002'::uuid,
    'aa000000-0000-0000-0000-000000000001'::uuid,
    'AF-LUCAS',
    'ACTIVE',
    'PLATA',
    NOW() - INTERVAL '6 months',
    'Bolivia',
    'Santa Cruz',
    'Embajador senior especializado en el sector tecnológico boliviano con red en La Paz, Cochabamba y Santa Cruz.',
    TRUE, TRUE, TRUE, 'es'
WHERE NOT EXISTS (
    SELECT 1 FROM ambassadors WHERE id = 'aa000000-0000-0000-0000-000000000002'::uuid
);

-- ─────────────────────────────────────────────────────────────
-- 3. LINKS DE REFERIDO
-- ─────────────────────────────────────────────────────────────
INSERT INTO ambassador_referral_links (id, ambassador_id, name, segment, city, code, url, clicks, conversions, active, created_at, updated_at)
VALUES
    ('aa000000-0000-0000-0004-000000000001'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid,
     'Link General Hardware', 'HARDWARE', 'Santa Cruz', 'LUCAS-HW-01',
     'https://techmarket.bo/ref/LUCAS-HW-01', 142, 9, TRUE,
     NOW() - INTERVAL '5 months', NOW() - INTERVAL '1 week'),
    ('aa000000-0000-0000-0004-000000000002'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid,
     'Link Software Cochabamba', 'SOFTWARE', 'Cochabamba', 'LUCAS-SW-CBB',
     'https://techmarket.bo/ref/LUCAS-SW-CBB', 87, 5, TRUE,
     NOW() - INTERVAL '4 months', NOW() - INTERVAL '2 weeks'),
    ('aa000000-0000-0000-0004-000000000003'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid,
     'Link Servicios La Paz', 'SERVICES', 'La Paz', 'LUCAS-SVC-LPZ',
     'https://techmarket.bo/ref/LUCAS-SVC-LPZ', 61, 3, TRUE,
     NOW() - INTERVAL '3 months', NOW() - INTERVAL '3 days')
ON CONFLICT (id) DO NOTHING;

-- ─────────────────────────────────────────────────────────────
-- 4. REFERIDOS (9 empresas, niveles 1-3)
-- ─────────────────────────────────────────────────────────────
INSERT INTO ambassador_referrals (
    id, ambassador_id, name, referral_type, contact_name, phone, email,
    city, country, status, created_at, last_activity_at
)
VALUES
    ('aa000000-0000-0000-0000-000000000011'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid,
     'TechStore Bolivia',      'HARDWARE',  'Mario Flores',    '+591 71234567', 'mario@techstore.bo',    'La Paz',     'Bolivia', 'ACTIVE', NOW() - INTERVAL '5 months',       NOW() - INTERVAL '3 days'),
    ('aa000000-0000-0000-0000-000000000012'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid,
     'SoftWave Solutions',     'SOFTWARE',  'Ana Quiroga',     '+591 72345678', 'ana@softwave.bo',       'Cochabamba', 'Bolivia', 'ACTIVE', NOW() - INTERVAL '4 months',       NOW() - INTERVAL '5 days'),
    ('aa000000-0000-0000-0000-000000000013'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid,
     'DataLink Servicios',     'SERVICES',  'Carlos Mendez',   '+591 73456789', 'carlos@datalink.bo',    'Santa Cruz', 'Bolivia', 'ACTIVE', NOW() - INTERVAL '3 months',       NOW() - INTERVAL '1 week'),
    ('aa000000-0000-0000-0000-000000000014'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid,
     'Innovatech SRL',         'SOFTWARE',  'Sofia Balcazar',  '+591 74567890', 'sofia@innovatech.bo',   'La Paz',     'Bolivia', 'ACTIVE', NOW() - INTERVAL '2 months',       NOW() - INTERVAL '4 days'),
    ('aa000000-0000-0000-0000-000000000015'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid,
     'BizConnect Bolivia',     'SERVICES',  'Juan Perez',      '+591 75678901', 'juan@bizconnect.bo',    'Santa Cruz', 'Bolivia', 'ACTIVE', NOW() - INTERVAL '3 months',       NOW() - INTERVAL '6 days'),
    ('aa000000-0000-0000-0000-000000000016'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid,
     'CloudPro Sistemas',      'SOFTWARE',  'Lucia Rios',      '+591 76789012', 'lucia@cloudpro.bo',     'Cochabamba', 'Bolivia', 'ACTIVE', NOW() - INTERVAL '2 months',       NOW() - INTERVAL '3 days'),
    ('aa000000-0000-0000-0000-000000000017'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid,
     'MegaRed Comunicaciones', 'HARDWARE',  'Pedro Vargas',    '+591 77890123', 'pedro@megared.bo',      'La Paz',     'Bolivia', 'ACTIVE', NOW() - INTERVAL '2 months',       NOW() - INTERVAL '2 days'),
    ('aa000000-0000-0000-0000-000000000018'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid,
     'AlphaData Corp',         'HARDWARE',  'Rosa Lima',       '+591 78901234', 'rosa@alphadata.bo',     'Santa Cruz', 'Bolivia', 'ACTIVE', NOW() - INTERVAL '1 month',        NOW() - INTERVAL '1 week'),
    ('aa000000-0000-0000-0000-000000000019'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid,
     'NetSolutions Andina',    'SERVICES',  'Diego Mamani',    '+591 79012345', 'diego@netsolutions.bo', 'Oruro',      'Bolivia', 'ACTIVE', NOW() - INTERVAL '1 month',        NOW() - INTERVAL '2 days')
ON CONFLICT (id) DO NOTHING;

-- ─────────────────────────────────────────────────────────────
-- 5. COMISIONES (niveles 1, 2 y 3)
-- ─────────────────────────────────────────────────────────────
INSERT INTO ambassador_commissions (
    id, ambassador_id, ambassador_referral_id,
    attribution_type, event_type, reference_type,
    amount, status, generated_at
)
VALUES
    -- R1 TechStore Bolivia (LEVEL1)
    ('aa000000-0000-0000-0001-000000000001'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid, 'aa000000-0000-0000-0000-000000000011'::uuid, 'LEVEL1', 'SUBSCRIPTION', 'HARDWARE', '850.00',  'CONFIRMED', NOW() - INTERVAL '5 months' + INTERVAL '5 days'),
    ('aa000000-0000-0000-0001-000000000002'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid, 'aa000000-0000-0000-0000-000000000011'::uuid, 'LEVEL1', 'RENEWAL',      'HARDWARE', '920.00',  'CONFIRMED', NOW() - INTERVAL '4 months' + INTERVAL '3 days'),
    ('aa000000-0000-0000-0001-000000000003'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid, 'aa000000-0000-0000-0000-000000000011'::uuid, 'LEVEL1', 'RENEWAL',      'HARDWARE', '970.00',  'CONFIRMED', NOW() - INTERVAL '3 months' + INTERVAL '7 days'),
    ('aa000000-0000-0000-0001-000000000004'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid, 'aa000000-0000-0000-0000-000000000011'::uuid, 'LEVEL1', 'RENEWAL',      'HARDWARE', '1050.00', 'CONFIRMED', NOW() - INTERVAL '2 months' + INTERVAL '4 days'),
    ('aa000000-0000-0000-0001-000000000005'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid, 'aa000000-0000-0000-0000-000000000011'::uuid, 'LEVEL1', 'RENEWAL',      'HARDWARE', '1120.00', 'CONFIRMED', NOW() - INTERVAL '3 weeks'  + INTERVAL '1 day'),
    ('aa000000-0000-0000-0001-000000000006'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid, 'aa000000-0000-0000-0000-000000000011'::uuid, 'LEVEL1', 'RENEWAL',      'HARDWARE', '1180.00', 'CONFIRMED', NOW() - INTERVAL '1 week'   + INTERVAL '2 days'),
    -- R2 SoftWave Solutions (LEVEL1)
    ('aa000000-0000-0000-0001-000000000007'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid, 'aa000000-0000-0000-0000-000000000012'::uuid, 'LEVEL1', 'SUBSCRIPTION', 'SOFTWARE', '650.00',  'CONFIRMED', NOW() - INTERVAL '4 months' + INTERVAL '7 days'),
    ('aa000000-0000-0000-0001-000000000008'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid, 'aa000000-0000-0000-0000-000000000012'::uuid, 'LEVEL1', 'RENEWAL',      'SOFTWARE', '710.00',  'CONFIRMED', NOW() - INTERVAL '3 months' + INTERVAL '4 days'),
    ('aa000000-0000-0000-0001-000000000009'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid, 'aa000000-0000-0000-0000-000000000012'::uuid, 'LEVEL1', 'RENEWAL',      'SOFTWARE', '760.00',  'CONFIRMED', NOW() - INTERVAL '2 months' + INTERVAL '2 days'),
    ('aa000000-0000-0000-0001-000000000010'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid, 'aa000000-0000-0000-0000-000000000012'::uuid, 'LEVEL1', 'RENEWAL',      'SOFTWARE', '800.00',  'CONFIRMED', NOW() - INTERVAL '4 weeks'  + INTERVAL '1 day'),
    ('aa000000-0000-0000-0001-000000000011'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid, 'aa000000-0000-0000-0000-000000000012'::uuid, 'LEVEL1', 'RENEWAL',      'SOFTWARE', '845.00',  'CONFIRMED', NOW() - INTERVAL '2 weeks'  + INTERVAL '3 days'),
    ('aa000000-0000-0000-0001-000000000012'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid, 'aa000000-0000-0000-0000-000000000012'::uuid, 'LEVEL1', 'RENEWAL',      'SOFTWARE', '890.00',  'CONFIRMED', NOW() - INTERVAL '4 days'),
    -- R3 DataLink Servicios (LEVEL1)
    ('aa000000-0000-0000-0001-000000000013'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid, 'aa000000-0000-0000-0000-000000000013'::uuid, 'LEVEL1', 'SUBSCRIPTION', 'SERVICES', '420.00',  'CONFIRMED', NOW() - INTERVAL '3 months' + INTERVAL '10 days'),
    ('aa000000-0000-0000-0001-000000000014'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid, 'aa000000-0000-0000-0000-000000000013'::uuid, 'LEVEL1', 'RENEWAL',      'SERVICES', '480.00',  'CONFIRMED', NOW() - INTERVAL '2 months' + INTERVAL '6 days'),
    ('aa000000-0000-0000-0001-000000000015'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid, 'aa000000-0000-0000-0000-000000000013'::uuid, 'LEVEL1', 'RENEWAL',      'SERVICES', '510.00',  'CONFIRMED', NOW() - INTERVAL '3 weeks'  + INTERVAL '2 days'),
    ('aa000000-0000-0000-0001-000000000016'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid, 'aa000000-0000-0000-0000-000000000013'::uuid, 'LEVEL1', 'RENEWAL',      'SERVICES', '545.00',  'CONFIRMED', NOW() - INTERVAL '1 week'   + INTERVAL '1 day'),
    -- R4 Innovatech SRL (LEVEL1)
    ('aa000000-0000-0000-0001-000000000017'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid, 'aa000000-0000-0000-0000-000000000014'::uuid, 'LEVEL1', 'SUBSCRIPTION', 'SOFTWARE', '730.00',  'CONFIRMED', NOW() - INTERVAL '2 months' + INTERVAL '3 days'),
    ('aa000000-0000-0000-0001-000000000018'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid, 'aa000000-0000-0000-0000-000000000014'::uuid, 'LEVEL1', 'RENEWAL',      'SOFTWARE', '780.00',  'CONFIRMED', NOW() - INTERVAL '2 weeks'  + INTERVAL '1 day'),
    ('aa000000-0000-0000-0001-000000000019'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid, 'aa000000-0000-0000-0000-000000000014'::uuid, 'LEVEL1', 'RENEWAL',      'SOFTWARE', '820.00',  'CONFIRMED', NOW() - INTERVAL '5 days'),
    -- R5 BizConnect Bolivia (LEVEL2)
    ('aa000000-0000-0000-0001-000000000020'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid, 'aa000000-0000-0000-0000-000000000015'::uuid, 'LEVEL2', 'SUBSCRIPTION', 'SERVICES', '480.00',  'CONFIRMED', NOW() - INTERVAL '3 months' + INTERVAL '8 days'),
    ('aa000000-0000-0000-0001-000000000021'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid, 'aa000000-0000-0000-0000-000000000015'::uuid, 'LEVEL2', 'RENEWAL',      'SERVICES', '520.00',  'CONFIRMED', NOW() - INTERVAL '2 months' + INTERVAL '5 days'),
    ('aa000000-0000-0000-0001-000000000022'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid, 'aa000000-0000-0000-0000-000000000015'::uuid, 'LEVEL2', 'RENEWAL',      'SERVICES', '560.00',  'CONFIRMED', NOW() - INTERVAL '3 weeks'  + INTERVAL '3 days'),
    ('aa000000-0000-0000-0001-000000000023'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid, 'aa000000-0000-0000-0000-000000000015'::uuid, 'LEVEL2', 'RENEWAL',      'SERVICES', '600.00',  'CONFIRMED', NOW() - INTERVAL '6 days'),
    -- R6 CloudPro Sistemas (LEVEL2)
    ('aa000000-0000-0000-0001-000000000024'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid, 'aa000000-0000-0000-0000-000000000016'::uuid, 'LEVEL2', 'SUBSCRIPTION', 'SOFTWARE', '610.00',  'CONFIRMED', NOW() - INTERVAL '2 months' + INTERVAL '9 days'),
    ('aa000000-0000-0000-0001-000000000025'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid, 'aa000000-0000-0000-0000-000000000016'::uuid, 'LEVEL2', 'RENEWAL',      'SOFTWARE', '655.00',  'CONFIRMED', NOW() - INTERVAL '4 weeks'  + INTERVAL '2 days'),
    ('aa000000-0000-0000-0001-000000000026'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid, 'aa000000-0000-0000-0000-000000000016'::uuid, 'LEVEL2', 'RENEWAL',      'SOFTWARE', '695.00',  'CONFIRMED', NOW() - INTERVAL '2 weeks'),
    ('aa000000-0000-0000-0001-000000000027'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid, 'aa000000-0000-0000-0000-000000000016'::uuid, 'LEVEL2', 'RENEWAL',      'SOFTWARE', '730.00',  'CONFIRMED', NOW() - INTERVAL '3 days'),
    -- R7 MegaRed Comunicaciones (LEVEL2)
    ('aa000000-0000-0000-0001-000000000028'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid, 'aa000000-0000-0000-0000-000000000017'::uuid, 'LEVEL2', 'SUBSCRIPTION', 'HARDWARE', '390.00',  'CONFIRMED', NOW() - INTERVAL '2 months' + INTERVAL '12 days'),
    ('aa000000-0000-0000-0001-000000000029'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid, 'aa000000-0000-0000-0000-000000000017'::uuid, 'LEVEL2', 'RENEWAL',      'HARDWARE', '430.00',  'CONFIRMED', NOW() - INTERVAL '3 weeks'),
    ('aa000000-0000-0000-0001-000000000030'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid, 'aa000000-0000-0000-0000-000000000017'::uuid, 'LEVEL2', 'RENEWAL',      'HARDWARE', '465.00',  'CONFIRMED', NOW() - INTERVAL '1 week'),
    -- R8 AlphaData Corp (LEVEL3)
    ('aa000000-0000-0000-0001-000000000031'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid, 'aa000000-0000-0000-0000-000000000018'::uuid, 'LEVEL3', 'SUBSCRIPTION', 'HARDWARE', '310.00',  'CONFIRMED', NOW() - INTERVAL '1 month'  + INTERVAL '4 days'),
    ('aa000000-0000-0000-0001-000000000032'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid, 'aa000000-0000-0000-0000-000000000018'::uuid, 'LEVEL3', 'RENEWAL',      'HARDWARE', '340.00',  'CONFIRMED', NOW() - INTERVAL '2 weeks'  + INTERVAL '2 days'),
    ('aa000000-0000-0000-0001-000000000033'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid, 'aa000000-0000-0000-0000-000000000018'::uuid, 'LEVEL3', 'RENEWAL',      'HARDWARE', '370.00',  'CONFIRMED', NOW() - INTERVAL '7 days'),
    -- R9 NetSolutions Andina (LEVEL3)
    ('aa000000-0000-0000-0001-000000000034'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid, 'aa000000-0000-0000-0000-000000000019'::uuid, 'LEVEL3', 'SUBSCRIPTION', 'SERVICES', '280.00',  'CONFIRMED', NOW() - INTERVAL '1 month'  + INTERVAL '6 days'),
    ('aa000000-0000-0000-0001-000000000035'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid, 'aa000000-0000-0000-0000-000000000019'::uuid, 'LEVEL3', 'RENEWAL',      'SERVICES', '310.00',  'CONFIRMED', NOW() - INTERVAL '2 weeks'),
    ('aa000000-0000-0000-0001-000000000036'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid, 'aa000000-0000-0000-0000-000000000019'::uuid, 'LEVEL3', 'RENEWAL',      'SERVICES', '335.00',  'CONFIRMED', NOW() - INTERVAL '2 days')
ON CONFLICT (id) DO NOTHING;

-- ─────────────────────────────────────────────────────────────
-- 6. ACTIVIDAD EN REFERIDOS
-- ─────────────────────────────────────────────────────────────
INSERT INTO ambassador_referral_activity (id, ambassador_referral_id, activity_type, description, created_at)
VALUES
    ('aa000000-0000-0000-0009-000000000001'::uuid, 'aa000000-0000-0000-0000-000000000011'::uuid, 'CALL',    'Llamada de seguimiento mensual, cliente satisfecho.',            NOW() - INTERVAL '3 days'),
    ('aa000000-0000-0000-0009-000000000002'::uuid, 'aa000000-0000-0000-0000-000000000011'::uuid, 'EMAIL',   'Envío de newsletter con novedades de hardware.',                 NOW() - INTERVAL '10 days'),
    ('aa000000-0000-0000-0009-000000000003'::uuid, 'aa000000-0000-0000-0000-000000000012'::uuid, 'MEETING', 'Reunión de onboarding completada exitosamente.',                 NOW() - INTERVAL '5 days'),
    ('aa000000-0000-0000-0009-000000000004'::uuid, 'aa000000-0000-0000-0000-000000000013'::uuid, 'CALL',    'Llamada para verificar proceso de renovación.',                  NOW() - INTERVAL '1 week'),
    ('aa000000-0000-0000-0009-000000000005'::uuid, 'aa000000-0000-0000-0000-000000000014'::uuid, 'EMAIL',   'Presentación de nuevos módulos disponibles.',                    NOW() - INTERVAL '4 days'),
    ('aa000000-0000-0000-0009-000000000006'::uuid, 'aa000000-0000-0000-0000-000000000015'::uuid, 'VISIT',   'Visita presencial a la oficina del cliente.',                    NOW() - INTERVAL '6 days')
ON CONFLICT (id) DO NOTHING;

-- ─────────────────────────────────────────────────────────────
-- 7. NOTAS EN REFERIDOS
-- ─────────────────────────────────────────────────────────────
INSERT INTO ambassador_referral_notes (id, ambassador_referral_id, note, created_at)
VALUES
    ('aa000000-0000-0000-000a-000000000001'::uuid, 'aa000000-0000-0000-0000-000000000011'::uuid,
     'Cliente interesado en ampliar su flota de equipos para Q3. Contactar en junio para oferta especial.', NOW() - INTERVAL '2 weeks'),
    ('aa000000-0000-0000-000a-000000000002'::uuid, 'aa000000-0000-0000-0000-000000000012'::uuid,
     'Solicitaron demo del módulo de BI. Coordinar con equipo técnico.',                                    NOW() - INTERVAL '1 week'),
    ('aa000000-0000-0000-000a-000000000003'::uuid, 'aa000000-0000-0000-0000-000000000013'::uuid,
     'Requieren integración con su sistema contable actual. Revisar compatibilidad.',                        NOW() - INTERVAL '5 days'),
    ('aa000000-0000-0000-000a-000000000004'::uuid, 'aa000000-0000-0000-0000-000000000014'::uuid,
     'Potencial upgrade a plan Enterprise para el próximo trimestre.',                                       NOW() - INTERVAL '3 days')
ON CONFLICT (id) DO NOTHING;

-- ─────────────────────────────────────────────────────────────
-- 8. TAREAS DE ONBOARDING
-- ─────────────────────────────────────────────────────────────
INSERT INTO ambassador_onboarding_tasks (id, ambassador_referral_id, title, status, due_date, created_at, completed_at)
VALUES
    ('aa000000-0000-0000-000b-000000000001'::uuid, 'aa000000-0000-0000-0000-000000000011'::uuid, 'Completar registro de empresa',    'DONE',    (NOW() - INTERVAL '4 months 20 days')::date, NOW() - INTERVAL '5 months', NOW() - INTERVAL '4 months 20 days'),
    ('aa000000-0000-0000-000b-000000000002'::uuid, 'aa000000-0000-0000-0000-000000000011'::uuid, 'Configurar primer inventario',     'DONE',    (NOW() - INTERVAL '4 months 10 days')::date, NOW() - INTERVAL '5 months', NOW() - INTERVAL '4 months 10 days'),
    ('aa000000-0000-0000-000b-000000000003'::uuid, 'aa000000-0000-0000-0000-000000000012'::uuid, 'Definir usuarios del sistema',     'DONE',    (NOW() - INTERVAL '3 months 15 days')::date, NOW() - INTERVAL '4 months', NOW() - INTERVAL '3 months 15 days'),
    ('aa000000-0000-0000-000b-000000000004'::uuid, 'aa000000-0000-0000-0000-000000000013'::uuid, 'Integrar API de facturación',      'PENDING', (NOW() + INTERVAL '1 month')::date,          NOW() - INTERVAL '3 months', NULL),
    ('aa000000-0000-0000-000b-000000000005'::uuid, 'aa000000-0000-0000-0000-000000000014'::uuid, 'Capacitación inicial completada',  'DONE',    (NOW() - INTERVAL '1 month 5 days')::date,   NOW() - INTERVAL '2 months', NOW() - INTERVAL '1 month 5 days')
ON CONFLICT (id) DO NOTHING;

-- ─────────────────────────────────────────────────────────────
-- 9. HITOS DE ONBOARDING
-- ─────────────────────────────────────────────────────────────
INSERT INTO ambassador_onboarding_milestones (id, ambassador_referral_id, milestone_code, completed, completed_at)
VALUES
    ('aa000000-0000-0000-000c-000000000001'::uuid, 'aa000000-0000-0000-0000-000000000011'::uuid, 'REGISTRATION_COMPLETE', TRUE,  NOW() - INTERVAL '4 months 20 days'),
    ('aa000000-0000-0000-000c-000000000002'::uuid, 'aa000000-0000-0000-0000-000000000011'::uuid, 'FIRST_LOGIN',           TRUE,  NOW() - INTERVAL '4 months 18 days'),
    ('aa000000-0000-0000-000c-000000000003'::uuid, 'aa000000-0000-0000-0000-000000000011'::uuid, 'FIRST_PURCHASE',        TRUE,  NOW() - INTERVAL '4 months 10 days'),
    ('aa000000-0000-0000-000c-000000000004'::uuid, 'aa000000-0000-0000-0000-000000000012'::uuid, 'REGISTRATION_COMPLETE', TRUE,  NOW() - INTERVAL '3 months 15 days'),
    ('aa000000-0000-0000-000c-000000000005'::uuid, 'aa000000-0000-0000-0000-000000000012'::uuid, 'FIRST_LOGIN',           TRUE,  NOW() - INTERVAL '3 months 12 days'),
    ('aa000000-0000-0000-000c-000000000006'::uuid, 'aa000000-0000-0000-0000-000000000013'::uuid, 'REGISTRATION_COMPLETE', TRUE,  NOW() - INTERVAL '2 months 25 days'),
    ('aa000000-0000-0000-000c-000000000007'::uuid, 'aa000000-0000-0000-0000-000000000013'::uuid, 'FIRST_PURCHASE',        FALSE, NULL),
    ('aa000000-0000-0000-000c-000000000008'::uuid, 'aa000000-0000-0000-0000-000000000014'::uuid, 'REGISTRATION_COMPLETE', TRUE,  NOW() - INTERVAL '1 month 20 days')
ON CONFLICT (id) DO NOTHING;

-- ─────────────────────────────────────────────────────────────
-- 10. LEADS (prospectos)
-- ─────────────────────────────────────────────────────────────
INSERT INTO ambassador_leads (
    id, ambassador_id, name, lead_type, contact_name, phone, email,
    city, country, source, status, close_probability, notes, next_action, last_contact_at, created_at, updated_at
)
VALUES
    ('aa000000-0000-0000-0005-000000000001'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid,
     'Digital Hub SRL',    'SOFTWARE', 'Roberto Chávez',   '+591 71111111', 'roberto@digitalhub.bo',
     'La Paz',     'Bolivia', 'REFERRAL',  'enNegociacion', 75,
     'Empresa en crecimiento, busca ERP básico. Presupuesto confirmado.',
     'Enviar propuesta formal antes del 20 del mes.',
     NOW() - INTERVAL '2 days',  NOW() - INTERVAL '3 weeks',  NOW() - INTERVAL '2 days'),
    ('aa000000-0000-0000-0005-000000000002'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid,
     'TechPoint Equipos',  'HARDWARE', 'Valeria Soto',     '+591 72222222', 'valeria@techpoint.bo',
     'Santa Cruz', 'Bolivia', 'COLD_CALL', 'nuevo',          40,
     'Prospecto frio, necesita 2 seguimientos más.',
     'Llamar la próxima semana para agendar demo.',
     NOW() - INTERVAL '5 days',  NOW() - INTERVAL '2 weeks',  NOW() - INTERVAL '5 days'),
    ('aa000000-0000-0000-0005-000000000003'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid,
     'Connecta Servicios', 'SERVICES', 'Fernando Quispe',  '+591 73333333', 'fernando@connecta.bo',
     'Cochabamba', 'Bolivia', 'EVENT',     'calificado',     60,
     'Contactado en feria tecnológica. Alto interés en servicios de soporte.',
     'Enviar comparativo de planes de soporte.',
     NOW() - INTERVAL '1 week',  NOW() - INTERVAL '1 month',  NOW() - INTERVAL '1 week'),
    ('aa000000-0000-0000-0005-000000000004'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid,
     'Nexo Tech Bolivia',  'SOFTWARE', 'Paola Herrera',    '+591 74444444', 'paola@nexotech.bo',
     'La Paz',     'Bolivia', 'REFERRAL',  'convertido',     100,
     'Referido por TechStore Bolivia. Cerrado exitosamente.',
     NULL,
     NOW() - INTERVAL '3 weeks', NOW() - INTERVAL '2 months', NOW() - INTERVAL '3 weeks')
ON CONFLICT (id) DO NOTHING;

-- ─────────────────────────────────────────────────────────────
-- 11. ACTIVIDADES DE LEADS
-- ─────────────────────────────────────────────────────────────
INSERT INTO ambassador_lead_activities (id, lead_id, activity_type, note, created_at)
VALUES
    ('aa000000-0000-0000-000d-000000000001'::uuid, 'aa000000-0000-0000-0005-000000000001'::uuid, 'CALL',    'Primera llamada de presentación. Interés confirmado.',   NOW() - INTERVAL '3 weeks'),
    ('aa000000-0000-0000-000d-000000000002'::uuid, 'aa000000-0000-0000-0005-000000000001'::uuid, 'EMAIL',   'Envío de brochure y casos de éxito.',                    NOW() - INTERVAL '2 weeks'),
    ('aa000000-0000-0000-000d-000000000003'::uuid, 'aa000000-0000-0000-0005-000000000001'::uuid, 'MEETING', 'Demo del sistema. Solicitaron propuesta formal.',         NOW() - INTERVAL '2 days'),
    ('aa000000-0000-0000-000d-000000000004'::uuid, 'aa000000-0000-0000-0005-000000000002'::uuid, 'CALL',    'Llamada inicial. No disponible, dejar mensaje.',          NOW() - INTERVAL '5 days'),
    ('aa000000-0000-0000-000d-000000000005'::uuid, 'aa000000-0000-0000-0005-000000000003'::uuid, 'EVENT',   'Contacto en Expo Tech Cochabamba 2024.',                  NOW() - INTERVAL '1 month'),
    ('aa000000-0000-0000-000d-000000000006'::uuid, 'aa000000-0000-0000-0005-000000000004'::uuid, 'CALL',    'Cierre de venta confirmado. Referido registrado.',        NOW() - INTERVAL '3 weeks')
ON CONFLICT (id) DO NOTHING;

-- ─────────────────────────────────────────────────────────────
-- 12. MÉTODOS DE PAGO
-- ─────────────────────────────────────────────────────────────
INSERT INTO ambassador_payout_methods (id, ambassador_id, method_type, bank, account_number, holder_name, last4, is_default, created_at)
VALUES
    ('aa000000-0000-0000-0006-000000000001'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid,
     'BANK_TRANSFER', 'Banco Unión',      '1234567890', 'Lucas Embajador', '7890', TRUE,  NOW() - INTERVAL '5 months'),
    ('aa000000-0000-0000-0006-000000000002'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid,
     'BANK_TRANSFER', 'Banco Mercantil',  '9876543210', 'Lucas Embajador', '3210', FALSE, NOW() - INTERVAL '2 months')
ON CONFLICT (id) DO NOTHING;

-- ─────────────────────────────────────────────────────────────
-- 13. RETIROS (withdrawals)
-- ─────────────────────────────────────────────────────────────
INSERT INTO ambassador_withdrawals (id, ambassador_id, amount, currency, status, requested_at, estimated_at)
VALUES
    ('aa000000-0000-0000-0007-000000000001'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid, 2500.00, 'Bs', 'COMPLETED', NOW() - INTERVAL '3 months',        (NOW() - INTERVAL '2 months 25 days')::date),
    ('aa000000-0000-0000-0007-000000000002'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid, 1800.00, 'Bs', 'COMPLETED', NOW() - INTERVAL '1 month 15 days', (NOW() - INTERVAL '1 month 10 days')::date),
    ('aa000000-0000-0000-0007-000000000003'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid, 3200.00, 'Bs', 'PENDING',   NOW() - INTERVAL '3 days',          (NOW() + INTERVAL '7 days')::date)
ON CONFLICT (id) DO NOTHING;

-- ─────────────────────────────────────────────────────────────
-- 14. INVITACIONES ENVIADAS
-- ─────────────────────────────────────────────────────────────
INSERT INTO ambassador_invitations (id, ambassador_id, email, name, phone, status, created_at)
VALUES
    ('aa000000-0000-0000-0008-000000000001'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid, 'nuevo1@empresa.bo', 'Tech Soluciones Oruro', '+591 76543210', 'PENDING',  NOW() - INTERVAL '2 weeks'),
    ('aa000000-0000-0000-0008-000000000002'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid, 'nuevo2@empresa.bo', 'InnovaRed Beni',        '+591 76543211', 'ACCEPTED', NOW() - INTERVAL '1 month'),
    ('aa000000-0000-0000-0008-000000000003'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid, 'nuevo3@empresa.bo', 'DataFlex Tarija',       '+591 76543212', 'PENDING',  NOW() - INTERVAL '5 days')
ON CONFLICT (id) DO NOTHING;

-- ─────────────────────────────────────────────────────────────
-- 15. MISIONES
-- ─────────────────────────────────────────────────────────────
INSERT INTO ambassador_missions (
    id, ambassador_id, title, description, benefit,
    mission_type, priority, status, steps, completion_criteria, progress, created_at, updated_at
)
VALUES
    ('aa000000-0000-0000-0003-000000000001'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid,
     'Registra tu primer referido',
     'Invita a una empresa a registrarse en TechMarket usando tu código de embajador.',
     'Comisión de Bs 50 al confirmar el registro',
     'hardware', 'alta', 'completada',
     'Busca prospectos en tu red|Envía tu código de referido|Confirma el registro en plataforma',
     'Referido confirmado y activo en plataforma',
     1.00, NOW() - INTERVAL '5 months', NOW() - INTERVAL '4 months'),
    ('aa000000-0000-0000-0003-000000000002'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid,
     'Completa tu perfil de embajador',
     'Agrega foto, descripción profesional y configura tu método de pago preferido.',
     'Acceso a comisiones avanzadas y tier Plata',
     'software', 'normal', 'completada',
     'Agrega foto de perfil|Escribe tu descripción|Configura método de pago',
     'Perfil completado al 100%',
     1.00, NOW() - INTERVAL '5 months', NOW() - INTERVAL '4 months 15 days'),
    ('aa000000-0000-0000-0003-000000000003'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid,
     'Refiere 3 empresas de hardware',
     'Trae al menos 3 negocios del sector tecnológico en hardware para ampliar tu red de nivel 1.',
     'Bono de Bs 200 al completar los 3 referidos',
     'hardware', 'alta', 'completada',
     'Identifica 3 negocios de hardware en tu zona|Preséntales TechMarket|Confirma sus registros activos',
     '3 referidos de hardware con estado ACTIVE',
     1.00, NOW() - INTERVAL '3 months', NOW() - INTERVAL '2 months'),
    ('aa000000-0000-0000-0003-000000000004'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid,
     'Activa un referido en software',
     'Ayuda a una empresa de software a completar su proceso de onboarding en la plataforma.',
     'Comisión extra del 2% en sus primeras 3 renovaciones',
     'software', 'normal', 'enProgreso',
     'Selecciona un prospecto de software|Guía el proceso de onboarding|Confirma la activación completa',
     'Referido con onboarding al 100% y primer pago procesado',
     0.50, NOW() - INTERVAL '2 months', NOW() - INTERVAL '3 weeks'),
    ('aa000000-0000-0000-0003-000000000005'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid,
     'Capta 2 clientes de servicios técnicos',
     'Refiere empresas que necesiten soporte o mantenimiento tecnológico continuo.',
     'Bs 30 por cliente activo + 5% en contratos anuales',
     'servicios', 'normal', 'enProgreso',
     'Identifica empresas con necesidad de soporte|Presenta el catálogo de servicios TechMarket|Acompaña la firma del primer contrato',
     '2 referidos de servicios con contrato activo',
     0.50, NOW() - INTERVAL '1 month', NOW() - INTERVAL '1 week'),
    ('aa000000-0000-0000-0003-000000000006'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid,
     'Genera Bs 5.000 en comisiones',
     'Alcanza Bs 5.000 acumulados en comisiones confirmadas para subir al tier Oro.',
     'Ascenso a tier Oro + acceso a panel avanzado de métricas',
     'servicios', 'alta', 'disponible',
     'Revisa tu progreso en el dashboard|Enfócate en referidos de alto impacto|Mantén actividad constante',
     'Bs 5.000 en comisiones confirmadas acumuladas',
     NULL, NOW() - INTERVAL '2 weeks', NOW() - INTERVAL '5 days')
ON CONFLICT (id) DO NOTHING;

-- ─────────────────────────────────────────────────────────────
-- 16. OPORTUNIDADES DE MERCADO
-- ─────────────────────────────────────────────────────────────
INSERT INTO ambassador_opportunities (
    id, ambassador_id, opportunity_type, zone, description,
    potential, data_source, status, is_saved, detected_at, updated_at
)
VALUES
    ('aa000000-0000-0000-0002-000000000001'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid,
     'HARDWARE', 'Equipetrol, Santa Cruz',
     'Alta demanda de equipos de oficina en empresas nuevas del área. Zona con crecimiento empresarial acelerado y baja penetración de proveedores TI.',
     'alto', 'Demanda no cubierta', 'nueva', FALSE, NOW() - INTERVAL '2 days',  NOW() - INTERVAL '2 days'),
    ('aa000000-0000-0000-0002-000000000002'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid,
     'SOFTWARE', 'Plan 3000, Santa Cruz',
     '3 empresas de logística buscan software de gestión sin proveedor actual. Necesidad crítica de ERP básico detectada en búsquedas recientes.',
     'alto', 'Búsqueda activa detectada', 'nueva', TRUE, NOW() - INTERVAL '5 days',  NOW() - INTERVAL '5 days'),
    ('aa000000-0000-0000-0002-000000000003'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid,
     'SERVICES', 'Sopocachi, La Paz',
     'Demanda creciente de soporte técnico en zona empresarial. Varios negocios con equipos sin contrato de mantenimiento activo.',
     'medio', 'Tendencia de mercado', 'enSeguimiento', FALSE, NOW() - INTERVAL '8 days',  NOW() - INTERVAL '8 days'),
    ('aa000000-0000-0000-0002-000000000004'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid,
     'HARDWARE', 'Miraflores, La Paz',
     'Nuevas oficinas en construcción requieren equipamiento tecnológico completo. Oportunidad de entrada temprana antes de apertura.',
     'medio', 'Nuevas aperturas', 'nueva', FALSE, NOW() - INTERVAL '12 days', NOW() - INTERVAL '12 days'),
    ('aa000000-0000-0000-0002-000000000005'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid,
     'SOFTWARE', 'Cochabamba Centro',
     'Empresas comerciales sin sistema de facturación electrónica. Requisito regulatorio inminente genera urgencia de adopción.',
     'alto', 'Obligación regulatoria', 'nueva', TRUE, NOW() - INTERVAL '15 days', NOW() - INTERVAL '15 days'),
    ('aa000000-0000-0000-0002-000000000006'::uuid, 'aa000000-0000-0000-0000-000000000002'::uuid,
     'SERVICES', 'Calacoto, La Paz',
     'Zona residencial premium con demanda de mantenimiento tecnológico para home offices. Mercado con capacidad de pago alta.',
     'bajo', 'Demanda estacional', 'atendida', FALSE, NOW() - INTERVAL '20 days', NOW() - INTERVAL '20 days')
ON CONFLICT (id) DO NOTHING;

COMMIT;

-- =============================================================================
-- Verificación rápida:
-- SELECT id, email, status FROM users WHERE email = 'embajador.test@techmarket.com';
-- SELECT id, referral_code, status, level FROM ambassadors WHERE user_id = 'aa000000-0000-0000-0000-000000000001';
-- SELECT COUNT(*) FROM ambassador_referrals    WHERE ambassador_id = 'aa000000-0000-0000-0000-000000000002';
-- SELECT COUNT(*) FROM ambassador_commissions  WHERE ambassador_id = 'aa000000-0000-0000-0000-000000000002';
-- SELECT COUNT(*) FROM ambassador_missions     WHERE ambassador_id = 'aa000000-0000-0000-0000-000000000002';
-- SELECT COUNT(*) FROM ambassador_opportunities WHERE ambassador_id = 'aa000000-0000-0000-0000-000000000002';
-- =============================================================================
