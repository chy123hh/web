# Sentinel Gateway Rate Limit Test
Write-Host "=== Testing Sentinel Rate Limit via Gateway ===" -ForegroundColor Cyan

$loginBody = '{"username":"test","password":"test123"}'
$successCount = 0
$blockedCount = 0
$otherCount = 0

Write-Host "Sending 150 concurrent requests to http://localhost:8080/api/user/login..."

$jobs = 1..150 | ForEach-Object {
    Start-Job -ScriptBlock {
        param($body)
        try {
            $response = Invoke-WebRequest -Uri "http://localhost:8080/api/user/login" `
                -Method Post `
                -ContentType "application/json" `
                -Body $body `
                -TimeoutSec 5
            return @{StatusCode = $response.StatusCode}
        } catch {
            if ($_.Exception.Response) {
                return @{StatusCode = [int]$_.Exception.Response.StatusCode}
            }
            return @{StatusCode = 0}
        }
    } -ArgumentList $loginBody
}

$results = $jobs | Wait-Job -Timeout 60 | Receive-Job 2>$null
$jobs | Remove-Job -Force 2>$null

foreach ($result in $results) {
    $code = $result.StatusCode
    if ($code -eq 200) {
        $successCount++
    } elseif ($code -eq 429) {
        $blockedCount++
    } else {
        $otherCount++
    }
}

Write-Host "`n=== Test Results ===" -ForegroundColor Green
Write-Host "Total Requests: $($results.Count)"
Write-Host "Success (200): $successCount" -ForegroundColor Green
Write-Host "Rate Limited (429): $blockedCount" -ForegroundColor Red
Write-Host "Other: $otherCount" -ForegroundColor Yellow
Write-Host "=== Test Complete ===" -ForegroundColor Cyan
