<#
  Runs the AI service with a REAL LLM: Google AI Studio (Gemini), free tier,
  via its OpenAI-compatible endpoint. Profiles: dev,gemini.

  The API key is read from TechMarket-AI/.env.local (git-ignored), key GEMINI_API_KEY.
  Optionally GEMINI_MODEL (default gemini-2.5-flash-lite).

  Usage (from TechMarket-AI/):
    .\scripts\run-gemini.ps1
    .\scripts\run-gemini.ps1 -Port 8099
#>
param(
    [int]$Port = 8091
)

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
Set-Location $root

# Load secrets from .env.local into the process environment.
$envFile = Join-Path $root ".env.local"
if (-not (Test-Path $envFile)) {
    Write-Error ".env.local not found. Create it with GEMINI_API_KEY=... (see README)."
}
Get-Content $envFile | ForEach-Object {
    $line = $_.Trim()
    if ($line -and -not $line.StartsWith("#")) {
        $parts = $line.Split("=", 2)
        if ($parts.Length -eq 2) {
            [System.Environment]::SetEnvironmentVariable($parts[0].Trim(), $parts[1].Trim(), "Process")
        }
    }
}

if (-not $env:GEMINI_API_KEY) {
    Write-Error "GEMINI_API_KEY is empty. Set it in .env.local."
}

$modelLabel = if ([string]::IsNullOrEmpty($env:GEMINI_MODEL)) { "gemini-2.5-flash-lite (default)" } else { $env:GEMINI_MODEL }
Write-Host "Starting AI service with Gemini (free tier) on port $Port ..." -ForegroundColor Cyan
Write-Host "  Model: $modelLabel" -ForegroundColor DarkGray
Write-Host "  Swagger: http://localhost:$Port/swagger-ui/index.html" -ForegroundColor DarkGray

& .\mvnw.cmd -f modules/ai-bootstrap/pom.xml `
    -Dspring-boot.run.profiles=dev `
    "-Dspring-boot.run.arguments=--server.port=$Port" `
    -Dspotless.check.skip=true `
    spring-boot:run
