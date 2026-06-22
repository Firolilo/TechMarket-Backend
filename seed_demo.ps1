#Requires -Version 5.1
<#
.SYNOPSIS
  Reset (TRUNCATE de datos) + seeder demo COMPLETO POR ROL para TechMarket.

  - Vacia los datos de IAM e IA (Postgres en Docker) preservando tablas de
    referencia/catalogo y el historial de Flyway.
  - Siembra VARIOS usuarios por rol (cliente, empresa, embajador, especialista)
    con datos coherentes entre IAM e IA.

  CLAVE de consistencia: el UUID de cada usuario en TechMarket-IA SIEMPRE es el
  derivado del id entero de IAM -> 00000000-0000-0000-0000-<id padded a 12>.
  Asi el header X-User-Id (que el front toma de IAM) coincide con la fila de IA.

.NOTES
  Requiere los contenedores iam-postgres e ia-postgres corriendo.
  Uso:  pwsh ./seed_demo.ps1            (reset + seed)
        pwsh ./seed_demo.ps1 -NoReset   (solo seed, idempotente)
#>
param([switch]$NoReset)

$ErrorActionPreference = "Stop"

# ─── Configuracion ──────────────────────────────────────────────────────────
$IAM_CONTAINER = "iam-postgres"
$IAM_DB = "iam_service";          $IAM_USER = "iam_user"
$IA_CONTAINER = "ia-postgres"
$IA_DB = "TECHMARKET_ia";         $IA_USER = "TECHMARKET_ia_user"
$TENANT = "00000000-0000-0000-0000-000000000000"

function Invoke-Iam([string]$sql) { $sql | docker exec -i $IAM_CONTAINER psql -q -v ON_ERROR_STOP=1 -U $IAM_USER -d $IAM_DB | Out-Null }
function Invoke-Ia ([string]$sql) { $sql | docker exec -i $IA_CONTAINER  psql -q -v ON_ERROR_STOP=1 -U $IA_USER  -d $IA_DB  | Out-Null }
function Scalar-Iam([string]$sql) { (docker exec $IAM_CONTAINER psql -t -A -U $IAM_USER -d $IAM_DB -c $sql).Trim() }
function Scalar-Ia ([string]$sql) { (docker exec $IA_CONTAINER  psql -t -A -U $IA_USER  -d $IA_DB  -c $sql).Trim() }

# ─── Verificar contenedores ─────────────────────────────────────────────────
Write-Host "`n[0] Verificando contenedores..." -ForegroundColor Cyan
foreach ($c in @($IAM_CONTAINER, $IA_CONTAINER)) {
    if (-not (docker ps --filter "name=^/$c$" --format "{{.Names}}")) { Write-Error "Contenedor '$c' no esta corriendo." }
    Write-Host "  OK: $c"
}

# ─── RESET (TRUNCATE de datos, preservando referencia + Flyway) ──────────────
if (-not $NoReset) {
    Write-Host "`n[1] Vaciando datos (TRUNCATE) preservando referencia/catalogo..." -ForegroundColor Cyan

    $iamKeep = "'flyway_schema_history','iam_role','iam_permission','iam_module','iam_resource','iam_action','iam_field','iam_branch'"
    Invoke-Iam @"
DO `$`$
DECLARE r record;
BEGIN
  FOR r IN SELECT tablename FROM pg_tables
           WHERE schemaname='public' AND tablename NOT IN ($iamKeep) LOOP
    EXECUTE format('TRUNCATE TABLE %I RESTART IDENTITY CASCADE', r.tablename);
  END LOOP;
END `$`$;
"@
    Write-Host "  IAM: datos vaciados (roles/permios/modulos preservados)."

    $iaKeep = "'flyway_schema_history','business_categories','catalog_categories','brands','technical_component_types','compatibility_rules','commission_rules','subscription_plans','notification_channels','roles','permissions','role_permissions'"
    Invoke-Ia @"
DO `$`$
DECLARE r record;
BEGIN
  FOR r IN SELECT tablename FROM pg_tables
           WHERE schemaname='public' AND tablename NOT IN ($iaKeep) LOOP
    EXECUTE format('TRUNCATE TABLE %I RESTART IDENTITY CASCADE', r.tablename);
  END LOOP;
END `$`$;
"@
    Write-Host "  IA: datos vaciados (categorias/marcas/planes preservados)."
}

# ─── Extensiones + catalogo base + comunidades + roles IA ────────────────────
Write-Host "`n[2] Preparando catalogo base, comunidades y roles..." -ForegroundColor Cyan
Invoke-Iam "CREATE EXTENSION IF NOT EXISTS pgcrypto;"
Invoke-Ia @"
CREATE EXTENSION IF NOT EXISTS pgcrypto;

INSERT INTO business_categories (id, name, description) VALUES
 ('20000000-0000-0000-0000-000000000101','Retail tecnologico','Venta de hardware, perifericos y accesorios.'),
 ('20000000-0000-0000-0000-000000000102','Servicios tecnicos','Soporte, reparacion y mantenimiento.'),
 ('20000000-0000-0000-0000-000000000103','Integradores corporativos','Redes, seguridad e infraestructura.')
ON CONFLICT (id) DO NOTHING;

INSERT INTO catalog_categories (id, name, parent_category_id, item_type, slug) VALUES
 ('30000000-0000-0000-0000-000000000100','Computacion',NULL,'PRODUCT','computacion'),
 ('30000000-0000-0000-0000-000000000101','Laptops','30000000-0000-0000-0000-000000000100','PRODUCT','laptops'),
 ('30000000-0000-0000-0000-000000000102','Componentes PC','30000000-0000-0000-0000-000000000100','PRODUCT','componentes-pc'),
 ('30000000-0000-0000-0000-000000000103','Perifericos','30000000-0000-0000-0000-000000000100','PRODUCT','perifericos'),
 ('30000000-0000-0000-0000-000000000105','Servicios tecnicos',NULL,'SERVICE','servicios-tecnicos'),
 ('30000000-0000-0000-0000-000000000106','Soporte y mantenimiento','30000000-0000-0000-0000-000000000105','SERVICE','soporte-mantenimiento')
ON CONFLICT (id) DO NOTHING;

INSERT INTO brands (id, name, slug, logo_url) VALUES
 ('40000000-0000-0000-0000-000000000101','Lenovo','lenovo',NULL),
 ('40000000-0000-0000-0000-000000000102','Asus','asus',NULL),
 ('40000000-0000-0000-0000-000000000103','Logitech','logitech',NULL),
 ('40000000-0000-0000-0000-000000000104','TP-Link','tp-link',NULL),
 ('40000000-0000-0000-0000-000000000105','Kingston','kingston',NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO roles (id, name, description)
SELECT gen_random_uuid(), v.n, v.d FROM (VALUES
  ('cliente','Cliente de TechMarket'),
  ('empresa','Empresa registrada'),
  ('embajador','Embajador de TechMarket'),
  ('especialista','Especialista tecnico')
) AS v(n,d)
WHERE NOT EXISTS (SELECT 1 FROM roles r WHERE LOWER(r.name)=v.n);

INSERT INTO communities (id, name, description, members_count, created_at) VALUES
 ('8ee00000-0000-0000-0000-000000000001','Hardware Bolivia','Comunidad para hablar de laptops, componentes y armado de PCs.',0, NOW()-INTERVAL '3 months'),
 ('8ee00000-0000-0000-0000-000000000002','Soporte y Mantenimiento','Tips y consultas sobre reparacion y mantenimiento de equipos.',0, NOW()-INTERVAL '3 months'),
 ('8ee00000-0000-0000-0000-000000000003','Software y Redes','Discusiones sobre software, redes y soluciones empresariales.',0, NOW()-INTERVAL '2 months')
ON CONFLICT (id) DO NOTHING;
"@
Write-Host "  Catalogo, comunidades y roles listos."

# ─── Helpers de creacion de usuario (IAM -> UUID derivado -> IA) ─────────────
function New-User {
    param([string]$Email,[string]$Pass,[string]$Type,[string]$Role,
          [string]$First,[string]$Last,[string]$Phone,[string]$City)

    # IAM: usuario + credencial + rol + scope
    Invoke-Iam @"
INSERT INTO iam_user (tenant_id, username, email, first_name, last_name, phone, country, city, user_type, terms_accepted, active, version, created_at, last_modified_at)
SELECT '$TENANT','$Email','$Email','$First','$Last','$Phone','Bolivia','$City','$Type',TRUE,TRUE,0,NOW(),NOW()
WHERE NOT EXISTS (SELECT 1 FROM iam_user WHERE tenant_id='$TENANT' AND LOWER(username)=LOWER('$Email'));

INSERT INTO iam_user_credential (user_id, tenant_id, password_hash, created_at, last_modified_at)
SELECT u.id,'$TENANT',crypt('$Pass',gen_salt('bf',10)),NOW(),NOW() FROM iam_user u
WHERE u.tenant_id='$TENANT' AND LOWER(u.username)=LOWER('$Email')
  AND NOT EXISTS (SELECT 1 FROM iam_user_credential c WHERE c.user_id=u.id);

INSERT INTO iam_user_roles (user_id, role_id)
SELECT u.id, r.id FROM iam_user u JOIN iam_role r ON r.tenant_id='$TENANT' AND LOWER(r.name)=LOWER('$Role')
WHERE u.tenant_id='$TENANT' AND LOWER(u.username)=LOWER('$Email')
  AND NOT EXISTS (SELECT 1 FROM iam_user_roles ur WHERE ur.user_id=u.id AND ur.role_id=r.id);

INSERT INTO iam_user_scope (tenant_id, user_id, branch_id, scope_type, created_at)
SELECT '$TENANT', u.id, NULL, 'GLOBAL', NOW() FROM iam_user u
WHERE u.tenant_id='$TENANT' AND LOWER(u.username)=LOWER('$Email')
  AND NOT EXISTS (SELECT 1 FROM iam_user_scope s WHERE s.user_id=u.id AND UPPER(s.scope_type)='GLOBAL');
"@

    $iamId = Scalar-Iam "SELECT id FROM iam_user WHERE tenant_id='$TENANT' AND LOWER(username)=LOWER('$Email');"
    if (-not $iamId) { Write-Error "No se pudo crear/leer el usuario IAM '$Email'." }
    $uuid = "00000000-0000-0000-0000-" + $iamId.PadLeft(12, '0')

    # IA: usuario base + rol
    Invoke-Ia @"
INSERT INTO users (id, first_name, last_name, email, phone, password_hash, status, created_at, updated_at)
VALUES ('$uuid','$First','$Last','$Email','$Phone',crypt('$Pass',gen_salt('bf',10)),'ACTIVE',NOW()-INTERVAL '2 months',NOW())
ON CONFLICT (id) DO UPDATE SET first_name=EXCLUDED.first_name, last_name=EXCLUDED.last_name, email=EXCLUDED.email, updated_at=NOW();

INSERT INTO user_roles (id, user_id, role_id, is_active, assigned_at)
SELECT gen_random_uuid(), '$uuid', r.id, TRUE, NOW() FROM roles r WHERE LOWER(r.name)=LOWER('$Role')
  AND NOT EXISTS (SELECT 1 FROM user_roles ur WHERE ur.user_id='$uuid' AND ur.role_id=r.id);
"@
    Write-Host ("    {0,-34} IAM#{1,-4} UUID {2}" -f $Email, $iamId, $uuid)
    return $uuid
}

# ─── EMPRESA: tenant + sucursal + catalogo + posts ──────────────────────────
function New-Empresa {
    param([string]$Uuid,[int]$Idx,[string]$Business,[string]$Legal,[string]$Desc,[string]$City)
    $T  = "5ee00000-0000-0000-0000-00000000000$Idx"          # tenant
    $B  = "52e00000-0000-0000-000$Idx-000000000001"          # branch
    $L  = "6ee00000-0000-0000-000$Idx-00000000000"           # listing prefix
    Invoke-Ia @"
INSERT INTO tenants (id, business_name, legal_name, business_type, description, status, registered_at, created_at, updated_at)
VALUES ('$T','$Business','$Legal','RETAIL','$Desc','ACTIVE',NOW()-INTERVAL '4 months',NOW()-INTERVAL '4 months',NOW())
ON CONFLICT (id) DO UPDATE SET business_name=EXCLUDED.business_name, description=EXCLUDED.description;

INSERT INTO tenant_profiles (id, tenant_id, short_description, full_description, website_url, rating_average, reviews_count)
VALUES ('$T'::uuid, '$T', '$Desc', '$Desc Catalogo con stock local, garantia y soporte.', 'https://$($Business.ToLower().Replace(' ','')).example.bo', 4.6, 12)
ON CONFLICT (id) DO NOTHING;

INSERT INTO tenant_members (id, tenant_id, user_id, tenant_role, status, joined_at)
VALUES (gen_random_uuid(), '$T', '$Uuid', 'OWNER', 'ACTIVE', NOW()-INTERVAL '4 months')
ON CONFLICT DO NOTHING;

INSERT INTO branches (id, tenant_id, name, address, city, phone, opening_hours, is_main_branch, status, created_at)
VALUES ('$B','$T','$Business — Sucursal Central','Av. Principal 100','$City','+591 7010020$Idx','Lun-Vie 09:00-19:00',TRUE,'ACTIVE',NOW()-INTERVAL '4 months')
ON CONFLICT (id) DO NOTHING;

INSERT INTO listings (id, tenant_id, category_id, brand_id, listing_type, title, description, base_price, currency, status, is_visible, internal_sku, created_at, updated_at) VALUES
 ('${L}1','$T','30000000-0000-0000-0000-000000000101','40000000-0000-0000-0000-000000000101','PRODUCT','Lenovo ThinkPad E14 ($Business)','Laptop empresarial 14, Ryzen 7, 16GB RAM, SSD 512GB.', $((7100+$Idx*150)).00,'BOB','ACTIVE',TRUE,'SKU-$Idx-LEN14',NOW()-INTERVAL '3 months',NOW()),
 ('${L}2','$T','30000000-0000-0000-0000-000000000103','40000000-0000-0000-0000-000000000103','PRODUCT','Logitech MX Master 3S ($Business)','Mouse ergonomico premium 8000 DPI multi-dispositivo.', $((600+$Idx*20)).00,'BOB','ACTIVE',TRUE,'SKU-$Idx-MXM3S',NOW()-INTERVAL '2 months',NOW()),
 ('${L}3','$T','30000000-0000-0000-0000-000000000102','40000000-0000-0000-0000-000000000105','PRODUCT','Kingston FURY DDR5 16GB ($Business)','Memoria RAM DDR5 5200MHz CL40 alto rendimiento.', $((760+$Idx*15)).00,'BOB','ACTIVE',TRUE,'SKU-$Idx-FURY16',NOW()-INTERVAL '6 weeks',NOW()),
 ('${L}4','$T','30000000-0000-0000-0000-000000000106',NULL,'SERVICE','Mantenimiento preventivo ($Business)','Limpieza interna, cambio de pasta termica y diagnostico general.', $((170+$Idx*10)).00,'BOB','ACTIVE',TRUE,'SKU-$Idx-MANT',NOW()-INTERVAL '3 months',NOW())
ON CONFLICT (id) DO NOTHING;

INSERT INTO listing_images (id, listing_id, image_url, display_order, is_primary) VALUES
 (gen_random_uuid(),'${L}1','https://cdn.techmarket.local/l/$Idx-1.jpg','1',TRUE),
 (gen_random_uuid(),'${L}2','https://cdn.techmarket.local/l/$Idx-2.jpg','1',TRUE),
 (gen_random_uuid(),'${L}3','https://cdn.techmarket.local/l/$Idx-3.jpg','1',TRUE),
 (gen_random_uuid(),'${L}4','https://cdn.techmarket.local/l/$Idx-4.jpg','1',TRUE)
ON CONFLICT DO NOTHING;

INSERT INTO listing_specifications (id, listing_id, attribute_name, attribute_value, unit, is_normalized) VALUES
 (gen_random_uuid(),'${L}1','Procesador','AMD Ryzen 7 7730U',NULL,TRUE),
 (gen_random_uuid(),'${L}1','Memoria RAM','16','GB',TRUE),
 (gen_random_uuid(),'${L}3','Velocidad','5200','MHz',TRUE)
ON CONFLICT DO NOTHING;

INSERT INTO branch_listings (id, branch_id, listing_id, status, is_available, created_at) VALUES
 (gen_random_uuid(),'$B','${L}1','ACTIVE',TRUE,NOW()),
 (gen_random_uuid(),'$B','${L}2','ACTIVE',TRUE,NOW()),
 (gen_random_uuid(),'$B','${L}3','ACTIVE',TRUE,NOW()),
 (gen_random_uuid(),'$B','${L}4','ACTIVE',TRUE,NOW())
ON CONFLICT DO NOTHING;

INSERT INTO branch_inventory (id, listing_id, branch_id, stock_available, stock_reserved, minimum_stock, updated_at) VALUES
 (gen_random_uuid(),'${L}1','$B','6','1','2',NOW()),
 (gen_random_uuid(),'${L}2','$B','11','2','3',NOW()),
 (gen_random_uuid(),'${L}3','$B','18','3','5',NOW())
ON CONFLICT DO NOTHING;

INSERT INTO service_details (id, listing_id, estimated_duration_minutes, requires_diagnosis, offers_on_site_service, service_area, terms_and_conditions)
VALUES (gen_random_uuid(),'${L}4',90,'false','true','$City','Incluye limpieza y diagnostico. Repuestos aparte.')
ON CONFLICT DO NOTHING;

INSERT INTO feed_posts (id, tenant_id, author_user_id, post_type, title, content, status, created_at) VALUES
 (gen_random_uuid(),'$T','$Uuid','ANNOUNCEMENT','$Business ya esta en TechMarket','Encontranos con stock local, garantia y soporte tecnico post-venta.','PUBLISHED',NOW()-INTERVAL '5 weeks'),
 (gen_random_uuid(),'$T','$Uuid','PROMOTION','Descuentos de temporada en $Business','Aprovecha precios especiales en laptops y perifericos este mes.','PUBLISHED',NOW()-INTERVAL '1 week')
ON CONFLICT DO NOTHING;
"@
}

# ─── ESPECIALISTA: perfil + servicios + disponibilidad + cartera ─────────────
function New-Especialista {
    param([string]$Uuid,[string]$Specialty,[string]$Location)
    Invoke-Ia @"
INSERT INTO specialist_profiles (id, user_id, specialty, location, photo_url, created_at, updated_at)
VALUES (gen_random_uuid(), '$Uuid', '$Specialty', '$Location', NULL, NOW(), NOW())
ON CONFLICT ON CONSTRAINT uk_specialist_profiles_user_id DO NOTHING;

INSERT INTO specialist_services (id, user_id, name, description, price, currency, service_type, featured, created_at, updated_at)
SELECT gen_random_uuid(), '$Uuid', t.n, t.d, t.p, 'Bs', t.st, t.f, NOW(), NOW() FROM (VALUES
  ('Reparacion de laptops','Diagnostico y reparacion completa de laptops.',150.00,'HARDWARE',TRUE),
  ('Formateo e instalacion','Formateo con Windows y programas esenciales.',80.00,'SOFTWARE',FALSE),
  ('Configuracion de redes','Routers y redes domesticas o empresariales.',120.00,'REDES',TRUE)
) AS t(n,d,p,st,f)
WHERE NOT EXISTS (SELECT 1 FROM specialist_services s WHERE s.user_id='$Uuid');

INSERT INTO specialist_availability (id, user_id, status, days_json, start_time, end_time, modalities_json, coverage, response_time, created_at, updated_at)
VALUES (gen_random_uuid(), '$Uuid', 'disponible', '["lunes","martes","miercoles","jueves","viernes"]', '08:00','18:00', '["presencial","remoto"]', '$Location', '2 horas', NOW(), NOW())
ON CONFLICT ON CONSTRAINT uk_specialist_availability_user_id DO NOTHING;

INSERT INTO specialist_certifications (id, user_id, name, institution, obtained_at, file_url, status, created_at, updated_at)
SELECT gen_random_uuid(), '$Uuid', t.n, t.i, t.o, NULL, t.s, NOW(), NOW() FROM (VALUES
  ('CompTIA A+','CompTIA','2023-06','verificado'),
  ('Cisco CCNA','Cisco','2024-01','pendiente')
) AS t(n,i,o,s)
WHERE NOT EXISTS (SELECT 1 FROM specialist_certifications c WHERE c.user_id='$Uuid');

INSERT INTO specialist_portfolio_items (id, user_id, title, service_name, result, work_date, created_at, updated_at)
SELECT gen_random_uuid(), '$Uuid', t.t, t.sn, t.r, t.wd::date, NOW(), NOW() FROM (VALUES
  ('Recuperacion de datos','Reparacion de laptops','Datos recuperados al 100%','2025-03-15'),
  ('Red para 20 equipos','Configuracion de redes','Red estable y segmentada','2025-04-20')
) AS t(t,sn,r,wd)
WHERE NOT EXISTS (SELECT 1 FROM specialist_portfolio_items p WHERE p.user_id='$Uuid');
"@
}

# ─── EMBAJADOR: perfil + links + referidos + comisiones + misiones ──────────
function New-Embajador {
    param([string]$Uuid,[int]$Idx,[string]$Code,[string]$Level,[string]$City)
    $A = "aae00000-0000-0000-0000-00000000000$Idx"           # ambassador id
    Invoke-Ia @"
INSERT INTO ambassadors (id, user_id, referral_code, status, level, activated_at, country, city, description, email_notifications, push_notifications, public_profile, language)
VALUES ('$A','$Uuid','$Code','ACTIVE','$Level',NOW()-INTERVAL '6 months','Bolivia','$City','Embajador del sector tecnologico boliviano.',TRUE,TRUE,TRUE,'es')
ON CONFLICT (id) DO NOTHING;

INSERT INTO ambassador_referral_links (id, ambassador_id, name, segment, city, code, url, clicks, conversions, active, created_at, updated_at) VALUES
 (gen_random_uuid(),'$A','Link Hardware','HARDWARE','$City','$Code-HW','https://techmarket.bo/ref/$Code-HW',142,9,TRUE,NOW()-INTERVAL '5 months',NOW()),
 (gen_random_uuid(),'$A','Link Software','SOFTWARE','$City','$Code-SW','https://techmarket.bo/ref/$Code-SW',87,5,TRUE,NOW()-INTERVAL '4 months',NOW()),
 (gen_random_uuid(),'$A','Link Servicios','SERVICES','$City','$Code-SV','https://techmarket.bo/ref/$Code-SV',61,3,TRUE,NOW()-INTERVAL '3 months',NOW())
ON CONFLICT DO NOTHING;
"@
    # Referidos + comisiones (5 referidos, una comision por cada uno)
    $refs = @(
        @{n="TechStore Bolivia"; t="HARDWARE"; c="Mario Flores"; ci="La Paz"; amt=850},
        @{n="SoftWave Solutions"; t="SOFTWARE"; c="Ana Quiroga"; ci="Cochabamba"; amt=650},
        @{n="DataLink Servicios"; t="SERVICES"; c="Carlos Mendez"; ci="Santa Cruz"; amt=420},
        @{n="Innovatech SRL"; t="SOFTWARE"; c="Sofia Balcazar"; ci="La Paz"; amt=730},
        @{n="MegaRed Comunicaciones"; t="HARDWARE"; c="Pedro Vargas"; ci="La Paz"; amt=390}
    )
    $n = 0
    foreach ($r in $refs) {
        $n++
        $rid = ("aae00000-0000-0000-00{0}0-0000000000{1:D2}" -f $Idx, $n)
        Invoke-Ia @"
INSERT INTO ambassador_referrals (id, ambassador_id, name, referral_type, contact_name, phone, email, city, country, status, created_at, last_activity_at)
VALUES ('$rid','$A','$($r.n)','$($r.t)','$($r.c)','+591 7$Idx$($n)00000','ref$Idx$n@empresa.bo','$($r.ci)','Bolivia','ACTIVE',NOW()-INTERVAL '$($n+1) months',NOW()-INTERVAL '$n weeks')
ON CONFLICT (id) DO NOTHING;
INSERT INTO ambassador_commissions (id, ambassador_id, ambassador_referral_id, attribution_type, event_type, reference_type, amount, status, generated_at)
VALUES (gen_random_uuid(),'$A','$rid','LEVEL1','SUBSCRIPTION','$($r.t)','$($r.amt).00','CONFIRMED',NOW()-INTERVAL '$n months')
ON CONFLICT DO NOTHING;
"@
    }
    Invoke-Ia @"
INSERT INTO ambassador_missions (id, ambassador_id, title, description, benefit, mission_type, priority, status, steps, completion_criteria, progress, created_at, updated_at) VALUES
 (gen_random_uuid(),'$A','Registra tu primer referido','Invita a una empresa usando tu codigo.','Comision de Bs 50','hardware','alta','completada','Busca prospectos|Envia tu codigo|Confirma registro','Referido activo',1.00,NOW()-INTERVAL '5 months',NOW()-INTERVAL '4 months'),
 (gen_random_uuid(),'$A','Genera Bs 5.000 en comisiones','Alcanza Bs 5.000 confirmados para subir de tier.','Ascenso de tier','servicios','alta','disponible','Revisa tu dashboard|Enfocate en alto impacto|Manten actividad','Bs 5.000 confirmados',NULL,NOW()-INTERVAL '2 weeks',NOW())
ON CONFLICT DO NOTHING;

INSERT INTO ambassador_opportunities (id, ambassador_id, opportunity_type, zone, description, potential, data_source, status, is_saved, detected_at, updated_at) VALUES
 (gen_random_uuid(),'$A','HARDWARE','Equipetrol, Santa Cruz','Alta demanda de equipos de oficina en empresas nuevas.','alto','Demanda no cubierta','nueva',FALSE,NOW()-INTERVAL '2 days',NOW()-INTERVAL '2 days'),
 (gen_random_uuid(),'$A','SOFTWARE','Cochabamba Centro','Empresas sin facturacion electronica; urgencia regulatoria.','alto','Obligacion regulatoria','nueva',TRUE,NOW()-INTERVAL '15 days',NOW()-INTERVAL '15 days')
ON CONFLICT DO NOTHING;

INSERT INTO ambassador_payout_methods (id, ambassador_id, method_type, bank, account_number, holder_name, last4, is_default, created_at)
VALUES (gen_random_uuid(),'$A','BANK_TRANSFER','Banco Union','1234567890','Embajador $Idx','7890',TRUE,NOW()-INTERVAL '5 months')
ON CONFLICT DO NOTHING;

INSERT INTO ambassador_withdrawals (id, ambassador_id, amount, currency, status, requested_at, estimated_at)
VALUES (gen_random_uuid(),'$A',2500.00,'Bs','COMPLETED',NOW()-INTERVAL '3 months',(NOW()-INTERVAL '2 months')::date)
ON CONFLICT DO NOTHING;
"@
}

# ─── CLIENTE: direcciones + resenas + favoritos + comunidad ─────────────────
function New-Cliente {
    param([string]$Uuid,[int]$Idx,[string]$City)
    # Referencia productos de la empresa 1 (tenant 5ee...001, listings 6ee...0001-000N)
    $T1 = "5ee00000-0000-0000-0000-000000000001"
    $P1 = "6ee00000-0000-0000-0001-000000000001"   # laptop empresa 1
    $P2 = "6ee00000-0000-0000-0001-000000000002"   # mouse empresa 1
    Invoke-Ia @"
INSERT INTO client_addresses (id, user_id, title, country, city, address, reference, default_address, created_at, updated_at) VALUES
 (gen_random_uuid(),'$Uuid','Casa','Bolivia','$City','Av. 6 de Agosto $Idx`23','Edificio, piso 3',TRUE,NOW()-INTERVAL '2 months',NOW()),
 (gen_random_uuid(),'$Uuid','Trabajo','Bolivia','$City','Calle Comercio $Idx`45','Oficina 5',FALSE,NOW()-INTERVAL '6 weeks',NOW())
ON CONFLICT DO NOTHING;

INSERT INTO reviews (id, tenant_id, user_id, listing_id, rating, comment, moderation_status, created_at) VALUES
 (gen_random_uuid(),'$T1','$Uuid','$P1',5.00,'Excelente laptop, muy rapida y llego en perfectas condiciones.','APPROVED',NOW()-INTERVAL '12 days'),
 (gen_random_uuid(),'$T1','$Uuid','$P2',4.00,'Muy buen mouse, comodo para largas jornadas.','APPROVED',NOW()-INTERVAL '8 days')
ON CONFLICT DO NOTHING;

INSERT INTO favorites (id, user_id, tenant_id, listing_id, created_at) VALUES
 (gen_random_uuid(),'$Uuid','$T1','$P1',NOW()-INTERVAL '8 days'),
 (gen_random_uuid(),'$Uuid','$T1',NULL,NOW()-INTERVAL '4 days')
ON CONFLICT DO NOTHING;

INSERT INTO community_memberships (id, community_id, user_id, joined_at) VALUES
 (gen_random_uuid(),'8ee00000-0000-0000-0000-000000000001','$Uuid',NOW()-INTERVAL '7 days'),
 (gen_random_uuid(),'8ee00000-0000-0000-0000-000000000002','$Uuid',NOW()-INTERVAL '5 days')
ON CONFLICT (community_id, user_id) DO NOTHING;

INSERT INTO feed_posts (id, tenant_id, author_user_id, post_type, title, content, status, created_at, community_id)
VALUES (gen_random_uuid(),NULL,'$Uuid','COMMUNITY','Mi experiencia comprando en TechMarket','Compre una laptop y todo el proceso fue rapido y confiable. Recomendado.','PUBLISHED',NOW()-INTERVAL '6 days','8ee00000-0000-0000-0000-000000000001')
ON CONFLICT DO NOTHING;

INSERT INTO notifications (id, user_id, notification_type, title, message, channel, is_read, sent_at, link_url) VALUES
 (gen_random_uuid(),'$Uuid','ORDER_DELIVERED','Pedido entregado','Tu pedido fue entregado. Deja tu resena!','IN_APP',TRUE,NOW()-INTERVAL '15 days','/cliente/pedidos/$oid'),
 (gen_random_uuid(),'$Uuid','COMMUNITY','Nueva actividad en tu comunidad','Hay nuevas publicaciones en Hardware Bolivia.','IN_APP',FALSE,NOW()-INTERVAL '2 days','/cliente/comunidades')
ON CONFLICT DO NOTHING;
"@
}

# ─── ORQUESTACION ────────────────────────────────────────────────────────────
Write-Host "`n[3] Sembrando EMPRESAS..." -ForegroundColor Cyan
$e1 = New-User -Email "empresa.test@techmarket.com"  -Pass "Empresa123!" -Type "empresa" -Role "empresa" -First "Andes Tech Store" -Last "Bolivia" -Phone "+591 70100201" -City "La Paz"
New-Empresa -Uuid $e1 -Idx 1 -Business "Andes Tech Store" -Legal "Andes Tech Store SRL" -Desc "Laptops, componentes y perifericos con stock local." -City "La Paz"
$e2 = New-User -Email "empresa2.test@techmarket.com" -Pass "Empresa123!" -Type "empresa" -Role "empresa" -First "ByteLab Store" -Last "Bolivia" -Phone "+591 70100202" -City "Santa Cruz"
New-Empresa -Uuid $e2 -Idx 2 -Business "ByteLab Store" -Legal "ByteLab Store SRL" -Desc "Equipos, accesorios y servicio tecnico especializado." -City "Santa Cruz"

Write-Host "`n[4] Sembrando ESPECIALISTAS..." -ForegroundColor Cyan
$s1 = New-User -Email "especialista.test@techmarket.com"  -Pass "Especialista123!" -Type "especialista" -Role "especialista" -First "Carlos" -Last "Techero" -Phone "+591 70123456" -City "La Paz"
New-Especialista -Uuid $s1 -Specialty "Reparacion y mantenimiento de computadoras" -Location "La Paz, Bolivia"
$s2 = New-User -Email "especialista2.test@techmarket.com" -Pass "Especialista123!" -Type "especialista" -Role "especialista" -First "Valeria" -Last "Soto" -Phone "+591 70123457" -City "Cochabamba"
New-Especialista -Uuid $s2 -Specialty "Redes y soporte de infraestructura" -Location "Cochabamba, Bolivia"

Write-Host "`n[5] Sembrando EMBAJADORES..." -ForegroundColor Cyan
$a1 = New-User -Email "embajador.test@techmarket.com"  -Pass "Embajador123!" -Type "embajador" -Role "embajador" -First "Lucas" -Last "Embajador" -Phone "+591 71234567" -City "Santa Cruz"
New-Embajador -Uuid $a1 -Idx 1 -Code "AF-LUCAS" -Level "PLATA" -City "Santa Cruz"
$a2 = New-User -Email "embajador2.test@techmarket.com" -Pass "Embajador123!" -Type "embajador" -Role "embajador" -First "Mariana" -Last "Lopez" -Phone "+591 71234568" -City "La Paz"
New-Embajador -Uuid $a2 -Idx 2 -Code "AF-MARI" -Level "BRONCE" -City "La Paz"

Write-Host "`n[6] Sembrando CLIENTES..." -ForegroundColor Cyan
$c1 = New-User -Email "cliente.test@techmarket.com"  -Pass "Cliente123!" -Type "cliente" -Role "cliente" -First "Sofia" -Last "Flores" -Phone "+59171234567" -City "La Paz"
New-Cliente -Uuid $c1 -Idx 1 -City "La Paz"
$c2 = New-User -Email "cliente2.test@techmarket.com" -Pass "Cliente123!" -Type "cliente" -Role "cliente" -First "Diego" -Last "Ramirez" -Phone "+59171234568" -City "Santa Cruz"
New-Cliente -Uuid $c2 -Idx 2 -City "Santa Cruz"
$c3 = New-User -Email "cliente3.test@techmarket.com" -Pass "Cliente123!" -Type "cliente" -Role "cliente" -First "Camila" -Last "Vargas" -Phone "+59171234569" -City "Cochabamba"
New-Cliente -Uuid $c3 -Idx 3 -City "Cochabamba"

# Recalcular contadores de comunidad
Invoke-Ia "UPDATE communities SET members_count = (SELECT COUNT(*) FROM community_memberships m WHERE m.community_id = communities.id);"

# ─── VERIFICACION ─────────────────────────────────────────────────────────────
Write-Host "`n[7] Verificacion:" -ForegroundColor Cyan
Write-Host ("  IAM usuarios     : " + (Scalar-Iam "SELECT count(*) FROM iam_user;"))
Write-Host ("  IA  usuarios     : " + (Scalar-Ia  "SELECT count(*) FROM users;"))
Write-Host ("  IA  tenants      : " + (Scalar-Ia  "SELECT count(*) FROM tenants;"))
Write-Host ("  IA  listings     : " + (Scalar-Ia  "SELECT count(*) FROM listings;"))
Write-Host ("  IA  ambassadors  : " + (Scalar-Ia  "SELECT count(*) FROM ambassadors;"))
Write-Host ("  IA  specialists  : " + (Scalar-Ia  "SELECT count(*) FROM specialist_profiles;"))
Write-Host ("  IA  reviews      : " + (Scalar-Ia  "SELECT count(*) FROM reviews;"))

# ─── INGESTA DEL INDICE SEMANTICO (marketplace RAG) ─────────────────────────
# Trae el catalogo real de IA (8082) y lo manda al indice vectorial de AI (8091).
# Requiere IA + AI levantados (start-all.ps1 / docker compose up) y GEMINI_API_KEY en el AI
# (los embeddings se generan al indexar). Es best-effort: si el AI no esta arriba, se omite.
$IaBase = $env:IA_BASE_URL; if (-not $IaBase) { $IaBase = "http://localhost:8082" }
$AiBase = $env:AI_BASE_URL; if (-not $AiBase) { $AiBase = "http://localhost:8091" }
Write-Host "`n[8] Ingesta del indice semantico del marketplace:" -ForegroundColor Cyan
try {
    $catalog = Invoke-RestMethod -Method Get -Uri "$IaBase/api/marketplace/catalogo-indexable" -TimeoutSec 15
    $payload = @{ documentos = $catalog } | ConvertTo-Json -Depth 6
    $res = Invoke-RestMethod -Method Post -Uri "$AiBase/api/v1/ai/marketplace/index" -Body $payload -ContentType "application/json" -TimeoutSec 120
    Write-Host ("  Indexados        : " + $res.indexed + " items del catalogo (empresas + especialistas)") -ForegroundColor Green
} catch {
    Write-Host "  (omitido: AI/IA no disponibles o sin GEMINI_API_KEY). Reindexar luego con:" -ForegroundColor Yellow
    Write-Host "    Invoke-RestMethod GET  $IaBase/api/marketplace/catalogo-indexable | %% { @{documentos=`$_} | ConvertTo-Json -Depth 6 } | Invoke-RestMethod POST $AiBase/api/v1/ai/marketplace/index -ContentType application/json"
}

Write-Host "`n=====================================================" -ForegroundColor Green
Write-Host " Seed demo completado. Credenciales (mismo password por rol):" -ForegroundColor Green
Write-Host "=====================================================" -ForegroundColor Green
Write-Host "  EMPRESA      empresa.test / empresa2.test           -> Empresa123!"
Write-Host "  ESPECIALISTA especialista.test / especialista2.test -> Especialista123!"
Write-Host "  EMBAJADOR    embajador.test / embajador2.test       -> Embajador123!"
Write-Host "  CLIENTE      cliente.test / cliente2.test / cliente3.test -> Cliente123!"
Write-Host "  (dominio @techmarket.com)`n"

