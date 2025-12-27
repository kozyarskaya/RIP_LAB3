# load_test.ps1
$url = "http://localhost:8081/api/quote"
$total = 100
$start = Get-Date

Write-Host "Тест нагрузки: $total запросов к $url"
Write-Host "Старт: $start"

$success = 0
$times = @()

for ($i = 1; $i -le $total; $i++) {
    $requestStart = Get-Date
    
    try {
        $response = Invoke-RestMethod -Uri $url -Method Get -TimeoutSec 5
        $success++
        
        $duration = ((Get-Date) - $requestStart).TotalMilliseconds
        $times += $duration
        
        if ($i % 100 -eq 0) {
            Write-Host "  Обработано $i/$total запросов"
        }
        
        # Небольшая пауза между запросами
        Start-Sleep -Milliseconds 10
    }
    catch {
        Write-Host "  Ошибка запроса $i : $_" -ForegroundColor Red
    }
}

$end = Get-Date
$totalTime = ($end - $start).TotalSeconds

Write-Host "`n=== РЕЗУЛЬТАТЫ ==="
Write-Host "Время выполнения: $totalTime сек"
Write-Host "Успешных запросов: $success из $total"
Write-Host "Среднее время ответа: $(($times | Measure-Object -Average).Average) мс"
Write-Host "Мин/Макс: $(($times | Measure-Object -Minimum).Minimum)/$(($times | Measure-Object -Maximum).Maximum) мс"