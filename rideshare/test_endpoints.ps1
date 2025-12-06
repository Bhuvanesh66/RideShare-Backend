# Test endpoints for RideShare API

$baseUrl = "http://localhost:8081"
$headers = @{"Content-Type"="application/json"}

Write-Host "====== RIDESHARE API ENDPOINT TESTS ======"
Write-Host ""

# TEST 1: Register USER
Write-Host "TEST 1: POST /api/auth/register (USER)" -ForegroundColor Cyan
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/api/auth/register" -Method POST `
        -Headers $headers -Body '{"username":"john","password":"1234","role":"ROLE_USER"}'
    $userId = ($response.Content | ConvertFrom-Json).id
    Write-Host "✓ SUCCESS - User registered with ID: $userId" -ForegroundColor Green
    Write-Host "Response: $($response.Content)" -ForegroundColor Gray
} catch {
    Write-Host "✗ FAILED - $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# TEST 2: Register DRIVER
Write-Host "TEST 2: POST /api/auth/register (DRIVER)" -ForegroundColor Cyan
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/api/auth/register" -Method POST `
        -Headers $headers -Body '{"username":"driver1","password":"abcd","role":"ROLE_DRIVER"}'
    $driverId = ($response.Content | ConvertFrom-Json).id
    Write-Host "✓ SUCCESS - Driver registered with ID: $driverId" -ForegroundColor Green
    Write-Host "Response: $($response.Content)" -ForegroundColor Gray
} catch {
    Write-Host "✗ FAILED - $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# TEST 3: Login USER and get JWT
Write-Host "TEST 3: POST /api/auth/login (get JWT token)" -ForegroundColor Cyan
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/api/auth/login" -Method POST `
        -Headers $headers -Body '{"username":"john","password":"1234"}'
    $token = ($response.Content | ConvertFrom-Json).token
    Write-Host "✓ SUCCESS - JWT Token obtained" -ForegroundColor Green
    Write-Host "Token: $($token.Substring(0, 50))..." -ForegroundColor Gray
} catch {
    Write-Host "✗ FAILED - $($_.Exception.Message)" -ForegroundColor Red
    $token = $null
}
Write-Host ""

# TEST 4: Create Ride (USER)
if ($token) {
    Write-Host "TEST 4: POST /api/v1/rides (Create Ride)" -ForegroundColor Cyan
    try {
        $authHeader = @{"Authorization"="Bearer $token"; "Content-Type"="application/json"}
        $response = Invoke-WebRequest -Uri "$baseUrl/api/v1/rides" -Method POST `
            -Headers $authHeader -Body '{"pickupLocation":"Koramangala","dropLocation":"Indiranagar"}'
        $rideId = ($response.Content | ConvertFrom-Json).id
        Write-Host "✓ SUCCESS - Ride created with ID: $rideId" -ForegroundColor Green
        Write-Host "Response: $($response.Content)" -ForegroundColor Gray
    } catch {
        Write-Host "✗ FAILED - $($_.Exception.Message)" -ForegroundColor Red
        $rideId = $null
    }
} else {
    Write-Host "TEST 4: SKIPPED - No JWT token" -ForegroundColor Yellow
}
Write-Host ""

# TEST 5: Get User Rides
if ($token) {
    Write-Host "TEST 5: GET /api/v1/user/rides (View My Rides)" -ForegroundColor Cyan
    try {
        $authHeader = @{"Authorization"="Bearer $token"}
        $response = Invoke-WebRequest -Uri "$baseUrl/api/v1/user/rides" -Method GET `
            -Headers $authHeader
        Write-Host "✓ SUCCESS - Retrieved user rides" -ForegroundColor Green
        Write-Host "Response: $($response.Content)" -ForegroundColor Gray
    } catch {
        Write-Host "✗ FAILED - $($_.Exception.Message)" -ForegroundColor Red
    }
} else {
    Write-Host "TEST 5: SKIPPED - No JWT token" -ForegroundColor Yellow
}
Write-Host ""

# TEST 6: Login DRIVER and get JWT
Write-Host "TEST 6: POST /api/auth/login (DRIVER - get JWT)" -ForegroundColor Cyan
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/api/auth/login" -Method POST `
        -Headers $headers -Body '{"username":"driver1","password":"abcd"}'
    $driverToken = ($response.Content | ConvertFrom-Json).token
    Write-Host "✓ SUCCESS - Driver JWT obtained" -ForegroundColor Green
    Write-Host "Token: $($driverToken.Substring(0, 50))..." -ForegroundColor Gray
} catch {
    Write-Host "✗ FAILED - $($_.Exception.Message)" -ForegroundColor Red
    $driverToken = $null
}
Write-Host ""

# TEST 7: Get Pending Rides (DRIVER)
if ($driverToken) {
    Write-Host "TEST 7: GET /api/v1/driver/rides/requests (View Pending Requests)" -ForegroundColor Cyan
    try {
        $authHeader = @{"Authorization"="Bearer $driverToken"}
        $response = Invoke-WebRequest -Uri "$baseUrl/api/v1/driver/rides/requests" -Method GET `
            -Headers $authHeader
        Write-Host "✓ SUCCESS - Retrieved pending rides" -ForegroundColor Green
        Write-Host "Response: $($response.Content)" -ForegroundColor Gray
    } catch {
        Write-Host "✗ FAILED - $($_.Exception.Message)" -ForegroundColor Red
    }
} else {
    Write-Host "TEST 7: SKIPPED - No Driver JWT token" -ForegroundColor Yellow
}
Write-Host ""

# TEST 8: Driver Accepts Ride
if ($driverToken -and $rideId) {
    Write-Host "TEST 8: POST /api/v1/driver/rides/{id}/accept (Accept Ride)" -ForegroundColor Cyan
    try {
        $authHeader = @{"Authorization"="Bearer $driverToken"; "Content-Type"="application/json"}
        $response = Invoke-WebRequest -Uri "$baseUrl/api/v1/driver/rides/$rideId/accept" -Method POST `
            -Headers $authHeader
        Write-Host "✓ SUCCESS - Ride accepted" -ForegroundColor Green
        Write-Host "Response: $($response.Content)" -ForegroundColor Gray
    } catch {
        Write-Host "✗ FAILED - $($_.Exception.Message)" -ForegroundColor Red
    }
} else {
    Write-Host "TEST 8: SKIPPED - Missing rideId or driverToken" -ForegroundColor Yellow
}
Write-Host ""

# TEST 9: Complete Ride
if ($token -and $rideId) {
    Write-Host "TEST 9: POST /api/v1/rides/{id}/complete (Complete Ride)" -ForegroundColor Cyan
    try {
        $authHeader = @{"Authorization"="Bearer $token"; "Content-Type"="application/json"}
        $response = Invoke-WebRequest -Uri "$baseUrl/api/v1/rides/$rideId/complete" -Method POST `
            -Headers $authHeader
        Write-Host "✓ SUCCESS - Ride completed" -ForegroundColor Green
        Write-Host "Response: $($response.Content)" -ForegroundColor Gray
    } catch {
        Write-Host "✗ FAILED - $($_.Exception.Message)" -ForegroundColor Red
    }
} else {
    Write-Host "TEST 9: SKIPPED - Missing rideId or token" -ForegroundColor Yellow
}
Write-Host ""

Write-Host "====== TESTS COMPLETED ======" -ForegroundColor Green
