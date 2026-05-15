# Sentinel Rate Limit Test Script
Write-Host "Sentinel Rate Limit Test Starting" -ForegroundColor Cyan

$successCount = 0
$blockedCount = 0

# Send concurrent requests
$jobs = 1..150 | ForEach-Object {
    Start-Job -ScriptBlock {
        try {
            $response = Invoke-WebRequest -Uri "http://localhost:8081/api/user/profile" -Method Get -TimeoutSec 5
            return @{StatusCode = $response.StatusCode}
        } catch {
            if ($_.Exception.Response) {
                $statusCode = [int]$_.Exception.Response.StatusCode
                return @{StatusCode = $statusCode}
            }
            return @{StatusCode = 0}
        }
    }
}

# Wait and collect results
$results = $jobs | Wait-Job | Receive-Job
$jobs | Remove-Job

# Count results
foreach ($result in $results) {
    $code = $result.StatusCode
    if ($code -eq 200) {
        $successCount++
    } elseif ($code -eq 429) {
        $blockedCount++
    }
}

Write-Host "Total Requests: $($results.Count)"
Write-Host "Success (200): $successCount" -ForegroundColor Green
Write-Host "Blocked (429): $blockedCount" -ForegroundColor Red
Write-Host "Test Complete" -ForegroundColor Cyan
