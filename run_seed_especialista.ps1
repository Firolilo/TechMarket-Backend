# Seed especialista de prueba en ambas bases de datos
# Requiere: Docker corriendo con los contenedores del proyecto

$IAM_CONTAINER = "iam-postgres"
$IAM_USER      = "iam_user"
$IAM_DB        = "iam_service"
$IAM_SEED      = "$PSScriptRoot\TechMarket-IAM\seed_especialista_test.sql"

$IA_CONTAINER  = "ia-postgres"
$IA_USER       = "TECHMARKET_ia_user"
$IA_DB         = "TECHMARKET_ia"
$IA_SEED       = "$PSScriptRoot\TechMarket-IA\seed_especialista_test.sql"

function Run-Seed {
    param(
        [string]$Container,
        [string]$User,
        [string]$Database,
        [string]$SeedFile,
        [string]$Label
    )

    Write-Host "`n[$Label] Verificando contenedor '$Container'..." -ForegroundColor Cyan

    $running = docker ps --filter "name=^${Container}$" --format "{{.Names}}" 2>$null
    if ($running -ne $Container) {
        Write-Host "  ERROR: El contenedor '$Container' no está corriendo." -ForegroundColor Red
        Write-Host "  Inicialo con: docker compose up -d" -ForegroundColor Yellow
        return $false
    }

    Write-Host "  Contenedor activo. Ejecutando seed..." -ForegroundColor Green
    Get-Content $SeedFile | docker exec -i $Container psql -U $User -d $Database
    if ($LASTEXITCODE -eq 0) {
        Write-Host "  Seed aplicado correctamente." -ForegroundColor Green
    } else {
        Write-Host "  ERROR al aplicar el seed (exit code $LASTEXITCODE)." -ForegroundColor Red
        return $false
    }
    return $true
}

Write-Host "========================================"
Write-Host "  Seed: Especialista de prueba"
Write-Host "  Usuario: especialista.test@techmarket.com"
Write-Host "  Password: Especialista123!"
Write-Host "========================================"

# Limpia inserciones previas con username incorrecto
Write-Host "`n[Limpieza] Eliminando seed anterior si existe..." -ForegroundColor Cyan
$cleanIAM = @"
DELETE FROM iam_user_scope WHERE user_id IN (
    SELECT id FROM iam_user WHERE LOWER(username) IN (
        LOWER('especialista.test'), LOWER('especialista.test@techmarket.com')
    ) AND tenant_id = '00000000-0000-0000-0000-000000000000'
);
DELETE FROM iam_user_roles WHERE user_id IN (
    SELECT id FROM iam_user WHERE LOWER(username) IN (
        LOWER('especialista.test'), LOWER('especialista.test@techmarket.com')
    ) AND tenant_id = '00000000-0000-0000-0000-000000000000'
);
DELETE FROM iam_user_credential WHERE user_id IN (
    SELECT id FROM iam_user WHERE LOWER(username) IN (
        LOWER('especialista.test'), LOWER('especialista.test@techmarket.com')
    ) AND tenant_id = '00000000-0000-0000-0000-000000000000'
);
DELETE FROM iam_user WHERE LOWER(username) IN (
    LOWER('especialista.test'), LOWER('especialista.test@techmarket.com')
) AND tenant_id = '00000000-0000-0000-0000-000000000000';
"@
$cleanIAM | docker exec -i $IAM_CONTAINER psql -U $IAM_USER -d $IAM_DB | Out-Null

$cleanIA = @"
DELETE FROM specialist_transactions  WHERE user_id = 'facade00-feed-4000-a000-000000000001';
DELETE FROM specialist_withdrawals   WHERE user_id = 'facade00-feed-4000-a000-000000000001';
DELETE FROM specialist_certifications WHERE user_id = 'facade00-feed-4000-a000-000000000001';
DELETE FROM specialist_portfolio_items WHERE user_id = 'facade00-feed-4000-a000-000000000001';
DELETE FROM specialist_services      WHERE user_id = 'facade00-feed-4000-a000-000000000001';
DELETE FROM specialist_availability  WHERE user_id = 'facade00-feed-4000-a000-000000000001';
DELETE FROM specialist_profiles      WHERE user_id = 'facade00-feed-4000-a000-000000000001';
DELETE FROM user_roles WHERE user_id = 'facade00-feed-4000-a000-000000000001';
DELETE FROM users WHERE id = 'facade00-feed-4000-a000-000000000001'
    OR LOWER(email) = LOWER('especialista.test@techmarket.com');
"@
$cleanIA | docker exec -i $IA_CONTAINER psql -U $IA_USER -d $IA_DB | Out-Null

Write-Host "  Limpieza completada." -ForegroundColor Green

$okIAM = Run-Seed -Container $IAM_CONTAINER -User $IAM_USER -Database $IAM_DB -SeedFile $IAM_SEED -Label "IAM"
$okIA  = Run-Seed -Container $IA_CONTAINER  -User $IA_USER  -Database $IA_DB  -SeedFile $IA_SEED  -Label "IA"

Write-Host ""
if ($okIAM -and $okIA) {
    Write-Host "Listo. Inicia sesion con:" -ForegroundColor Green
    Write-Host "  Email:    especialista.test@techmarket.com"
    Write-Host "  Password: Especialista123!"
} else {
    Write-Host "Seed incompleto. Revisa los errores de arriba." -ForegroundColor Red
}
