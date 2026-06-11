<#
  Stops the TechMarket dev services by freeing their ports (8080 IAM, 8082 IA, 8091 AI).
  Useful because start-all.ps1 launches each service in its own window.

  Usage:
    .\stop-all.ps1
#>
$ports = @(8080, 8082, 8091)

foreach ($p in $ports) {
    $conns = Get-NetTCPConnection -LocalPort $p -State Listen -ErrorAction SilentlyContinue
    if ($conns) {
        $conns | Select-Object -ExpandProperty OwningProcess -Unique | ForEach-Object {
            try {
                $proc = Get-Process -Id $_ -ErrorAction Stop
                Write-Host ("Stopping port {0} -> PID {1} ({2})" -f $p, $_, $proc.ProcessName) -ForegroundColor Yellow
                Stop-Process -Id $_ -Force -ErrorAction Stop
            } catch {
                Write-Host ("Could not stop PID {0} on port {1}: {2}" -f $_, $p, $_.Exception.Message) -ForegroundColor Red
            }
        }
    } else {
        Write-Host ("Port {0} already free." -f $p) -ForegroundColor DarkGray
    }
}

Write-Host "Done." -ForegroundColor Green
