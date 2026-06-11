<#
  Starts the TechMarket microservices for local development (dev profile).

  Topology:
    - TechMarket-Core (core-platform) is a SHARED LIBRARY, not a runnable service.
      It is installed into the local Maven repo so IA and IAM can resolve it.
    - IAM -> http://localhost:8080   (Swagger: /swagger-ui.html)        [H2 in-memory]
    - IA  -> http://localhost:8082   (Swagger: /swagger-ui.html)        [H2 in-memory]
    - AI  -> http://localhost:8091   (Swagger: /swagger-ui/index.html)  [stubs, no DB]

  None of the dev profiles need an external database (H2 / stubs), so this just works.
  Each service starts in its own PowerShell window; close the window or Ctrl+C to stop
  one, or run .\stop-all.ps1 to stop them all.

  Usage:
    .\start-all.ps1             # install Core, then start IAM, IA, AI
    .\start-all.ps1 -SkipCore   # skip the Core install (faster if unchanged)
    .\start-all.ps1 -Clean      # `mvn clean` each service first (drops stale build
                                #  artifacts; use this if Flyway reports a duplicate
                                #  migration version, which means target/ is stale)
#>
param(
    [switch]$SkipCore,
    [switch]$Clean
)

$ErrorActionPreference = "Stop"
$root = $PSScriptRoot
$cleanPrefix = if ($Clean) { 'clean ' } else { '' }

function Start-ServiceWindow {
    param(
        [string]$Name,
        [string]$Dir,
        [string]$Command,
        [int]$Port
    )
    $path = Join-Path $root $Dir
    Write-Host ("Starting {0} (port {1}) ..." -f $Name, $Port) -ForegroundColor Cyan
    $inner = "Set-Location '$path'; `$Host.UI.RawUI.WindowTitle = 'TechMarket $Name :$Port'; $Command"
    Start-Process powershell -ArgumentList @('-NoExit', '-NoProfile', '-Command', $inner) | Out-Null
}

# 1) Build & install the shared library (Core) so IA/IAM can resolve core-platform.
if (-not $SkipCore) {
    Write-Host "Installing shared library TechMarket-Core (core-platform) ..." -ForegroundColor Yellow
    & (Join-Path $root 'TechMarket-Core\mvnw.cmd') -f (Join-Path $root 'TechMarket-Core\pom.xml') -q -B install -DskipTests
    if ($LASTEXITCODE -ne 0) { throw "Core install failed (exit $LASTEXITCODE)" }
    Write-Host "Core installed." -ForegroundColor Green
}

# 2) Launch the three runnable services, each in its own window (dev profile).
#    -Dspotless.check.skip=true: format checks are a commit/CI concern, not a runtime
#    one; skipping keeps a stray formatting violation from blocking local startup.
Start-ServiceWindow -Name 'IAM' -Dir 'TechMarket-IAM' -Port 8080 `
    -Command (".\mvnw.cmd {0}-Dspring-boot.run.profiles=dev -Dspotless.check.skip=true spring-boot:run" -f $cleanPrefix)

Start-ServiceWindow -Name 'IA' -Dir 'TechMarket-IA' -Port 8082 `
    -Command (".\mvnw.cmd {0}-Dspring-boot.run.profiles=dev -Dspotless.check.skip=true spring-boot:run" -f $cleanPrefix)

# AI uses Gemini (free tier); run-gemini.ps1 loads TechMarket-AI/.env.local (GEMINI_API_KEY).
Start-ServiceWindow -Name 'AI' -Dir 'TechMarket-AI' -Port 8091 `
    -Command '.\scripts\run-gemini.ps1'

Write-Host ""
Write-Host "All services launching (each in its own window). Give them ~30-60s to boot." -ForegroundColor Green
Write-Host "  IAM  -> http://localhost:8080/swagger-ui.html"
Write-Host "  IA   -> http://localhost:8082/swagger-ui.html"
Write-Host "  AI   -> http://localhost:8091/swagger-ui/index.html"
Write-Host ""
Write-Host "Stop everything with: .\stop-all.ps1"
