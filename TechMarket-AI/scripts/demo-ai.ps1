<#
  Demo: calls the TechMarket AI service (Gemini) and prints real responses, so you
  can show others that it works. Lists the model and hits the key endpoints.

  Usage (from TechMarket-AI/ or anywhere):
    .\scripts\demo-ai.ps1
    .\scripts\demo-ai.ps1 -BaseUrl http://localhost:8091
    .\scripts\demo-ai.ps1 -DelaySeconds 2     # space calls out for the free-tier rate limit
#>
param(
    [string]$BaseUrl = "http://localhost:8091",
    [double]$DelaySeconds = 1
)

# Render Spanish accents correctly in the console.
try { [Console]::OutputEncoding = [System.Text.Encoding]::UTF8 } catch {}

function Write-Section($title) {
    Write-Host ""
    Write-Host ("=" * 72) -ForegroundColor DarkCyan
    Write-Host "  $title" -ForegroundColor Cyan
    Write-Host ("=" * 72) -ForegroundColor DarkCyan
}

function Get-ConfiguredModel {
    $envFile = Join-Path $PSScriptRoot "..\.env.local"
    if (Test-Path $envFile) {
        $line = Get-Content $envFile | Where-Object { $_ -match '^\s*GEMINI_MODEL\s*=' } | Select-Object -First 1
        if ($line) { return (($line -split '=', 2)[1]).Trim() }
    }
    return "gemini-2.5-flash-lite (default)"
}

function Invoke-Demo {
    param(
        [string]$Title,
        [string]$Method,
        [string]$Path,
        $Body
    )
    Write-Host ""
    Write-Host "* $Title" -ForegroundColor White
    Write-Host ("  {0} {1}" -f $Method, $Path) -ForegroundColor Yellow
    if ($null -ne $Body) {
        Write-Host ("  request : {0}" -f ($Body | ConvertTo-Json -Compress -Depth 8)) -ForegroundColor DarkGray
    }
    try {
        $params = @{
            Uri        = "$BaseUrl$Path"
            Method     = $Method
            Headers    = @{ "X-Tenant-Id" = "00000000-0000-0000-0000-000000000000" }
            TimeoutSec = 90
        }
        if ($null -ne $Body) {
            $params.Body = ($Body | ConvertTo-Json -Depth 8)
            $params.ContentType = "application/json; charset=utf-8"
        }
        $sw = [System.Diagnostics.Stopwatch]::StartNew()
        $resp = Invoke-RestMethod @params
        $sw.Stop()
        Write-Host ("  response ({0} ms):" -f $sw.ElapsedMilliseconds) -ForegroundColor DarkGray
        ($resp | ConvertTo-Json -Depth 10) -split "`n" | ForEach-Object { Write-Host "  $_" -ForegroundColor Green }
    }
    catch {
        $status = $null
        $bodyText = $null
        if ($_.Exception.Response) {
            try { $status = [int]$_.Exception.Response.StatusCode } catch {}
            try {
                $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
                $bodyText = $reader.ReadToEnd()
            }
            catch {}
        }
        if ($status -eq 503 -or ($bodyText -and $bodyText -match 'RATE_LIMITED|cuota|quota')) {
            Write-Host "  AVISO: cuota del free tier de Gemini agotada (gemini-2.5-flash-lite = 20 req/dia)." -ForegroundColor Magenta
            Write-Host "  Se reinicia cada dia. Espacia las llamadas o reintenta manana." -ForegroundColor Magenta
        }
        else {
            Write-Host ("  ERROR ({0}): {1}" -f $status, $_.Exception.Message) -ForegroundColor Red
        }
        if ($bodyText) { Write-Host "  $bodyText" -ForegroundColor DarkGray }
    }
    if ($DelaySeconds -gt 0) { Start-Sleep -Seconds $DelaySeconds }
}

# ---------------------------------------------------------------------------
Write-Section "TechMarket AI Service - Demo (Gemini)"
Write-Host "  Base URL : $BaseUrl"
Write-Host "  Provider : Google Gemini - free tier (OpenAI-compatible endpoint)"
Write-Host "  Model    : $(Get-ConfiguredModel)"
Write-Host "  Note     : free tier is ~20 requests/day for flash-lite; calls beyond that return 503." -ForegroundColor DarkGray

# 0) Health -----------------------------------------------------------------
Write-Section "Health check"
try {
    $health = Invoke-RestMethod "$BaseUrl/actuator/health" -TimeoutSec 10
    Write-Host "  status: $($health.status)" -ForegroundColor Green
}
catch {
    Write-Host "  Service not reachable at $BaseUrl." -ForegroundColor Red
    Write-Host "  Start it with:  docker compose up -d   (or  .\scripts\run-gemini.ps1 )" -ForegroundColor Red
    exit 1
}

# 1) Generic completion -----------------------------------------------------
Write-Section "1) Generic completion  ->  /api/v1/ai/complete"
Invoke-Demo -Title "Free-form prompt (proves it's a real LLM)" `
    -Method POST -Path "/api/v1/ai/complete" `
    -Body @{ prompt = "En una frase, que es TechMarket?" }

# 2) Company assistant ------------------------------------------------------
Write-Section "2) Company AI  ->  /api/empresa/ia/consulta"
Invoke-Demo -Title "Actionable business insight from real metrics" `
    -Method POST -Path "/api/empresa/ia/consulta" `
    -Body @{
        question = "Que accion priorizo hoy para subir mi conversion?"
        context  = @{ conversion = "3%"; chatsSinResponder = 12; publicacionTop = "Laptop Gamer" }
    }

# 3) Ambassador: prospect scoring -------------------------------------------
Write-Section "3) Ambassador AI  ->  /api/ambassadors/ai/prospect-score"
Invoke-Demo -Title "Score a prospect's potential, with weighted factors" `
    -Method POST -Path "/api/ambassadors/ai/prospect-score" `
    -Body @{ businessName = "TecnoStore"; category = "Electronica"; city = "Santa Cruz" }

# 4) Ambassador: proactive insights -----------------------------------------
Write-Section "4) Ambassador AI  ->  /api/ambassadors/ai/insights"
Invoke-Demo -Title "Proactive insights (id + timestamp added by the service)" `
    -Method GET -Path "/api/ambassadors/ai/insights"

# 5) Ambassador: follow-up message ------------------------------------------
Write-Section "5) Ambassador AI  ->  /api/ambassadors/ai/follow-up-suggestion"
Invoke-Demo -Title "Ready-to-send follow-up message for a referral" `
    -Method POST -Path "/api/ambassadors/ai/follow-up-suggestion" `
    -Body @{ referralId = "REF-123"; context = "Mostro interes pero no responde hace 5 dias" }

# 6) Specialist: dashboard insights -----------------------------------------
Write-Section "6) Specialist AI  ->  /api/specialists/ai/insights"
Invoke-Demo -Title "AI dashboard: suggested questions, scenarios and radar" `
    -Method GET -Path "/api/specialists/ai/insights"

# 7) Specialist: Q&A --------------------------------------------------------
Write-Section "7) Specialist AI  ->  /api/specialists/ai/query"
Invoke-Demo -Title "Specialist question -> structured advice" `
    -Method POST -Path "/api/specialists/ai/query" `
    -Body @{ consulta = "Como consigo mas clientes para mis servicios de desarrollo web?" }

Write-Section "Done"
Write-Host "  All responses above came from Google Gemini through the AI service." -ForegroundColor Green
Write-Host "  Swagger UI: $BaseUrl/swagger-ui/index.html" -ForegroundColor DarkGray
Write-Host ""
