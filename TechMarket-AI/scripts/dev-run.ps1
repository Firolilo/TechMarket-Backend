# Run script for ai-service in dev profile
# Usage: .\scripts\dev-run.ps1 [-Port 8082]
param(
    [int]$Port = 0
)

Write-Host "Starting ai-service in dev profile..." -ForegroundColor Cyan

$buildArgs = @(
    "-pl", "modules/ai-bootstrap",
    "-am",
    "install",
    "-DskipTests"
)

$runArgs = @(
    "-f", "modules/ai-bootstrap/pom.xml",
    "-Dspring-boot.run.profiles=dev"
)

if ($Port -gt 0) {
    Write-Host "Using custom port: $Port" -ForegroundColor Yellow
    $runArgs += "-Dspring-boot.run.arguments=--server.port=$Port"
}

$runArgs += "spring-boot:run"

.\mvnw.cmd $buildArgs
if ($LASTEXITCODE -ne 0) {
    exit $LASTEXITCODE
}

.\mvnw.cmd $runArgs
