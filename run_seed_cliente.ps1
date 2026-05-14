#Requires -Version 5.1
$ErrorActionPreference = "Stop"

# ─── Configuración ────────────────────────────────────────────────────────────
$IAM_CONTAINER  = "iam-postgres"
$IAM_DB         = "iam_service"
$IAM_USER       = "iam_user"

$IA_CONTAINER   = "TECHMARKET-ia-postgres"
$IA_DB          = "TECHMARKET_ia"
$IA_USER        = "TECHMARKET_ia_user"

$IAM_SEED = Join-Path $PSScriptRoot "TechMarket-IAM\seed_cliente_test.sql"
$IA_SEED  = Join-Path $PSScriptRoot "TechMarket-IA\seed_cliente_test.sql"

$CLIENT_EMAIL   = "cliente.test@techmarket.com"
$TENANT_ID      = "00000000-0000-0000-0000-000000000000"

# ─── Helpers ──────────────────────────────────────────────────────────────────
function Invoke-IamSql($sql) {
    docker exec -i $IAM_CONTAINER psql -U $IAM_USER -d $IAM_DB -c $sql
}

function Invoke-IaSql($sql) {
    docker exec -i $IA_CONTAINER psql -U $IA_USER -d $IA_DB -c $sql
}

function Invoke-IamFile($path) {
    Get-Content $path -Raw | docker exec -i $IAM_CONTAINER psql -U $IAM_USER -d $IAM_DB
}

# ─── Verificar contenedores ───────────────────────────────────────────────────
Write-Host "`n[1/5] Verificando contenedores..." -ForegroundColor Cyan

$iamRunning = docker ps --filter "name=^/${IAM_CONTAINER}$" --format "{{.Names}}" 2>$null
$iaRunning  = docker ps --filter "name=^/${IA_CONTAINER}$"  --format "{{.Names}}" 2>$null

if (-not $iamRunning) { Write-Error "Contenedor '$IAM_CONTAINER' no esta corriendo." }
if (-not $iaRunning)  { Write-Error "Contenedor '$IA_CONTAINER' no esta corriendo." }

Write-Host "  OK: $IAM_CONTAINER"
Write-Host "  OK: $IA_CONTAINER"

# ─── Limpiar seed anterior ────────────────────────────────────────────────────
Write-Host "`n[2/5] Limpiando seed anterior de '$CLIENT_EMAIL'..." -ForegroundColor Cyan

# TechMarket-IA: eliminar en orden respetando dependencias (por email via subquery)
$uuidLookup = "(SELECT id FROM users WHERE email = '$CLIENT_EMAIL')"
Invoke-IaSql "DELETE FROM notifications         WHERE user_id = $uuidLookup;" | Out-Null
Invoke-IaSql "DELETE FROM feed_posts            WHERE author_user_id = $uuidLookup;" | Out-Null
# Decrementar members_count antes de borrar membresias
Invoke-IaSql "UPDATE communities SET members_count = GREATEST(0, members_count - 1) WHERE id IN (SELECT community_id FROM community_memberships WHERE user_id = $uuidLookup);" | Out-Null
Invoke-IaSql "DELETE FROM community_memberships WHERE user_id = $uuidLookup;" | Out-Null
Invoke-IaSql "DELETE FROM favorites             WHERE user_id = $uuidLookup;" | Out-Null
Invoke-IaSql "DELETE FROM chat_read_receipts    WHERE user_id = $uuidLookup;" | Out-Null
Invoke-IaSql "DELETE FROM ticket_messages       WHERE ticket_id = 'c11e0600-feed-4001-a000-000000000001';" | Out-Null
Invoke-IaSql "DELETE FROM tickets               WHERE id = 'c11e0600-feed-4001-a000-000000000001';" | Out-Null
Invoke-IaSql "DELETE FROM reviews               WHERE user_id = $uuidLookup;" | Out-Null
Invoke-IaSql "DELETE FROM client_order_items    WHERE order_id IN ('c11e0300-feed-4001-a000-000000000001','c11e0300-feed-4001-a000-000000000002');" | Out-Null
Invoke-IaSql "DELETE FROM client_orders         WHERE user_id = $uuidLookup;" | Out-Null
Invoke-IaSql "DELETE FROM client_cart_items     WHERE user_id = $uuidLookup;" | Out-Null
Invoke-IaSql "DELETE FROM client_addresses      WHERE user_id = $uuidLookup;" | Out-Null
Invoke-IaSql "DELETE FROM users                 WHERE email   = '$CLIENT_EMAIL';" | Out-Null

# TechMarket-IAM
$lookupUser = "SELECT id FROM iam_user WHERE username = '$CLIENT_EMAIL' AND tenant_id = '$TENANT_ID'"
Invoke-IamSql "DELETE FROM iam_user_roles      WHERE user_id = ($lookupUser);" | Out-Null
Invoke-IamSql "DELETE FROM iam_user_scope      WHERE user_id = ($lookupUser);" | Out-Null
Invoke-IamSql "DELETE FROM iam_user_credential WHERE user_id = ($lookupUser);" | Out-Null
Invoke-IamSql "DELETE FROM iam_user            WHERE username = '$CLIENT_EMAIL' AND tenant_id = '$TENANT_ID';" | Out-Null

Write-Host "  Limpieza completada."

# ─── Seeder IAM ───────────────────────────────────────────────────────────────
Write-Host "`n[3/5] Ejecutando seed en TechMarket-IAM..." -ForegroundColor Cyan
Invoke-IamFile $IAM_SEED
Write-Host "  IAM seed OK."

# ─── Calcular UUID de TechMarket-IA ──────────────────────────────────────────
# iaApi.ts convierte "USR-013" -> "00000000-0000-0000-0000-000000000013"
# El UUID en TechMarket-IA debe coincidir con ese valor para que
# X-User-Id (del header) sea aceptado por UserHeaderConsistencyFilter.
Write-Host "`n[4/5] Calculando UUID del cliente para TechMarket-IA..." -ForegroundColor Cyan

$iamIdRaw = docker exec $IAM_CONTAINER psql -U $IAM_USER -d $IAM_DB -t -A `
    -c "SELECT id FROM iam_user WHERE username = '$CLIENT_EMAIL' AND tenant_id = '$TENANT_ID';"
$iamId = $iamIdRaw.Trim()

if (-not $iamId -or $iamId -notmatch '^\d+$') {
    Write-Error "No se pudo obtener el ID IAM del usuario '$CLIENT_EMAIL'. Valor recibido: '$iamId'"
}

$clientUuid = "00000000-0000-0000-0000-" + $iamId.PadLeft(12, '0')
Write-Host "  IAM user id : $iamId"
Write-Host "  UUID en IA  : $clientUuid"

# ─── Seeder TechMarket-IA ─────────────────────────────────────────────────────
Write-Host "`n[5/5] Ejecutando seed en TechMarket-IA..." -ForegroundColor Cyan
Get-Content $IA_SEED -Raw | docker exec -i $IA_CONTAINER psql -U $IA_USER -d $IA_DB -v "client_uuid=$clientUuid"
Write-Host "  IA seed OK."

# ─── Resumen ──────────────────────────────────────────────────────────────────
Write-Host "`n=====================================================" -ForegroundColor Green
Write-Host " Seed de cliente completado exitosamente." -ForegroundColor Green
Write-Host "=====================================================" -ForegroundColor Green
Write-Host ""
Write-Host " Credenciales de prueba:"
Write-Host "   Email   : $CLIENT_EMAIL"
Write-Host "   Password: Cliente123!"
Write-Host "   UUID IA : $clientUuid"
Write-Host ""
Write-Host " Datos sembrados:"
Write-Host "   - 1 usuario: Sofia Flores"
Write-Host "   - 2 direcciones (1 por defecto)"
Write-Host "   - 2 items en carrito"
Write-Host "   - 2 pedidos (1 DELIVERED, 1 PENDING)"
Write-Host "   - 2 resenas aprobadas"
Write-Host "   - 1 chat con ByteLab (3 mensajes)"
Write-Host "   - 3 favoritos (2 productos + 1 empresa)"
Write-Host "   - 2 membresias de comunidad"
Write-Host "   - 2 publicaciones en comunidades"
Write-Host "   - 3 notificaciones (1 sin leer)"
Write-Host ""
