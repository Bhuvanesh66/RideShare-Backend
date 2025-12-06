# Test the 3 fixes for authentication issues
$BASE_URL = "http://localhost:8081"

Write-Host "`n======================================" -ForegroundColor Cyan
Write-Host "TEST 1: Register Driver Returns Bearer Token" -ForegroundColor Yellow
Write-Host "======================================" -ForegroundColor Cyan

$username1 = "driver_fix_test_$(Get-Random -Minimum 1000 -Maximum 9999)"
$headers = @{"Content-Type" = "application/json"}
$body = @{
    username = $username1
    password = "pass1234"
    role = "ROLE_DRIVER"
} | ConvertTo-Json

$response = $null
$passed1 = $false

try {
    $response = Invoke-WebRequest -Uri "$BASE_URL/api/auth/register" -Method POST -Headers $headers -Body $body -ErrorAction Stop
    $data = $response.Content | ConvertFrom-Json
    
    if ($data.token) {
        Write-Host "PASS: Driver registration returned bearer token" -ForegroundColor Green
        Write-Host "  Status: $($response.StatusCode)" -ForegroundColor Gray
        $passed1 = $true
    }
} catch {
    Write-Host "FAIL: Error during registration" -ForegroundColor Red
}

Write-Host "`n======================================" -ForegroundColor Cyan
Write-Host "TEST 2: Duplicate Username is Rejected" -ForegroundColor Yellow
Write-Host "======================================" -ForegroundColor Cyan

$username2 = "user_dup_test_$(Get-Random -Minimum 1000 -Maximum 9999)"
$body1 = @{
    username = $username2
    password = "pass1234"
    role = "ROLE_USER"
} | ConvertTo-Json

$passed2 = $false

try {
    $response1 = Invoke-WebRequest -Uri "$BASE_URL/api/auth/register" -Method POST -Headers $headers -Body $body1 -ErrorAction Stop
    Write-Host "First registration successful" -ForegroundColor Green
    
    try {
        $response2 = Invoke-WebRequest -Uri "$BASE_URL/api/auth/register" -Method POST -Headers $headers -Body $body1 -ErrorAction Stop
        Write-Host "FAIL: Duplicate was accepted" -ForegroundColor Red
    } catch {
        if ($_.Exception.Response.StatusCode -eq 400) {
            Write-Host "PASS: Duplicate username rejected with 400" -ForegroundColor Green
            $passed2 = $true
        }
    }
} catch {
    Write-Host "FAIL: Error in duplicate test" -ForegroundColor Red
}

Write-Host "`n======================================" -ForegroundColor Cyan
Write-Host "TEST 3: Invalid Role is Rejected" -ForegroundColor Yellow
Write-Host "======================================" -ForegroundColor Cyan

$body3 = @{
    username = "user_invalid_role_$(Get-Random -Minimum 1000 -Maximum 9999)"
    password = "pass1234"
    role = "ROLE_DRIVER24"
} | ConvertTo-Json

$passed3 = $false

try {
    $response3 = Invoke-WebRequest -Uri "$BASE_URL/api/auth/register" -Method POST -Headers $headers -Body $body3 -ErrorAction Stop
    Write-Host "FAIL: Invalid role was accepted" -ForegroundColor Red
} catch {
    if ($_.Exception.Response.StatusCode -eq 400) {
        Write-Host "PASS: Invalid role rejected with 400" -ForegroundColor Green
        $passed3 = $true
    }
}

Write-Host "`n======================================" -ForegroundColor Cyan
Write-Host "SUMMARY" -ForegroundColor Cyan
Write-Host "======================================" -ForegroundColor Cyan
Write-Host "Test 1 (Bearer Token): $(if ($passed1) { 'PASS' } else { 'FAIL' })" -ForegroundColor $(if ($passed1) { 'Green' } else { 'Red' })
Write-Host "Test 2 (Duplicate Username): $(if ($passed2) { 'PASS' } else { 'FAIL' })" -ForegroundColor $(if ($passed2) { 'Green' } else { 'Red' })
Write-Host "Test 3 (Invalid Role): $(if ($passed3) { 'PASS' } else { 'FAIL' })" -ForegroundColor $(if ($passed3) { 'Green' } else { 'Red' })
Write-Host "======================================" -ForegroundColor Cyan

