# =============================================================================
# Ejecuta los seeders de embajador via docker exec (no requiere psql local)
# Contenedores: ia-postgres  /  iam-postgres
# =============================================================================

$ErrorActionPreference = "Stop"

# ── Contenedores y conexiones ─────────────────────────────────
$IA_CONTAINER  = "ia-postgres"
$IA_DB         = "TECHMARKET_ia"
$IA_USER       = "TECHMARKET_ia_user"

$IAM_CONTAINER = "iam-postgres"
$IAM_DB        = "iam_service"
$IAM_USER      = "iam_user"

# ── Rutas a los SQL ───────────────────────────────────────────
$SCRIPT_DIR = Split-Path -Parent $MyInvocation.MyCommand.Path
$IA_SQL  = Join-Path $SCRIPT_DIR "TechMarket-IA\seed_embajador_test.sql"
$IAM_SQL = Join-Path $SCRIPT_DIR "TechMarket-IAM\seed_embajador_test.sql"

# ── Verificar docker ──────────────────────────────────────────
if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
    Write-Error "docker no encontrado en el PATH."
    exit 1
}

function Run-Seeder {
    param(
        [string]$Label,
        [string]$Container,
        [string]$DB,
        [string]$User,
        [string]$SqlFile
    )

    Write-Host ""
    Write-Host "──────────────────────────────────────────" -ForegroundColor Cyan
    Write-Host " $Label" -ForegroundColor Cyan
    Write-Host "  Contenedor: $Container  DB: $DB" -ForegroundColor DarkGray
    Write-Host "──────────────────────────────────────────" -ForegroundColor Cyan

    # Verificar que el contenedor esté corriendo
    $running = docker ps --filter "name=^/${Container}$" --filter "status=running" -q
    if (-not $running) {
        Write-Host "[ERROR] El contenedor '$Container' no está corriendo." -ForegroundColor Red
        Write-Host "        Ejecuta 'docker compose up -d' en el proyecto correspondiente." -ForegroundColor Yellow
        exit 1
    }

    # Copiar el SQL al contenedor y ejecutarlo
    docker cp $SqlFile "${Container}:/tmp/seed_embajador.sql"
    if ($LASTEXITCODE -ne 0) { Write-Host "[ERROR] No se pudo copiar el archivo." -ForegroundColor Red; exit 1 }

    docker exec -i $Container psql -U $User -d $DB -f /tmp/seed_embajador.sql
    if ($LASTEXITCODE -ne 0) {
        Write-Host "[ERROR] Falló $Label (exit $LASTEXITCODE)" -ForegroundColor Red
        exit $LASTEXITCODE
    }

    Write-Host "[OK] $Label aplicado correctamente." -ForegroundColor Green
}

# ── Ejecutar ──────────────────────────────────────────────────
Run-Seeder `
    -Label     "TechMarket-IA  → seed_embajador_test.sql" `
    -Container $IA_CONTAINER  -DB $IA_DB  -User $IA_USER  `
    -SqlFile   $IA_SQL

Run-Seeder `
    -Label     "TechMarket-IAM → seed_embajador_test.sql" `
    -Container $IAM_CONTAINER -DB $IAM_DB -User $IAM_USER `
    -SqlFile   $IAM_SQL

Write-Host ""
Write-Host "============================================" -ForegroundColor Green
Write-Host " Seeders aplicados. Credenciales:" -ForegroundColor Green
Write-Host "   Email:    embajador.test@techmarket.com" -ForegroundColor White
Write-Host "   Password: Embajador123!" -ForegroundColor White
Write-Host "============================================" -ForegroundColor Green
