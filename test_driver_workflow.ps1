# ========================================
# COMPLETE DRIVER WORKFLOW TEST
# Steps: 1) Register Driver, 2) Register User, 3) Create Ride (User),
#        4) View Pending Requests (Driver), 5) Accept Ride (Driver),
#        6) Complete Ride (User/Driver)
# ========================================

$BASE_URL = "http://localhost:8081"
$DRIVER_USERNAME = "driver_john_$(Get-Random -Minimum 1000 -Maximum 9999)"
$USER_USERNAME = "user_jane_$(Get-Random -Minimum 1000 -Maximum 9999)"
$DRIVER_PASSWORD = "password123"
$USER_PASSWORD = "password123"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "STEP 1: Register Driver" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan

$registerDriverBody = @{
    username = $DRIVER_USERNAME
    password = $DRIVER_PASSWORD
    role = "ROLE_DRIVER"
} | ConvertTo-Json

$registerDriverResponse = curl -s -X POST "$BASE_URL/api/auth/register" `
    -H "Content-Type: application/json" `
    -d $registerDriverBody

Write-Host "Response: $registerDriverResponse" -ForegroundColor Green
$driverRegData = $registerDriverResponse | ConvertFrom-Json -ErrorAction SilentlyContinue

if ($null -eq $driverRegData.message) {
    Write-Host "✗ Failed to register driver" -ForegroundColor Red
    exit 1
}

Write-Host "✓ Driver registered successfully" -ForegroundColor Green
Write-Host "  Username: $DRIVER_USERNAME" -ForegroundColor Cyan

# --------

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "STEP 2: Register User (Passenger)" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan

$registerUserBody = @{
    username = $USER_USERNAME
    password = $USER_PASSWORD
    role = "ROLE_USER"
} | ConvertTo-Json

$registerUserResponse = curl -s -X POST "$BASE_URL/api/auth/register" `
    -H "Content-Type: application/json" `
    -d $registerUserBody

Write-Host "Response: $registerUserResponse" -ForegroundColor Green
$userRegData = $registerUserResponse | ConvertFrom-Json -ErrorAction SilentlyContinue

if ($null -eq $userRegData.message) {
    Write-Host "✗ Failed to register user" -ForegroundColor Red
    exit 1
}

Write-Host "✓ User registered successfully" -ForegroundColor Green
Write-Host "  Username: $USER_USERNAME" -ForegroundColor Cyan

# --------

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "STEP 3: User Login" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan

$userLoginBody = @{
    username = $USER_USERNAME
    password = $USER_PASSWORD
} | ConvertTo-Json

$userLoginResponse = curl -s -X POST "$BASE_URL/api/auth/login" `
    -H "Content-Type: application/json" `
    -d $userLoginBody

Write-Host "Response: $userLoginResponse" -ForegroundColor Green
$userLoginData = $userLoginResponse | ConvertFrom-Json -ErrorAction SilentlyContinue

if ($null -eq $userLoginData.token) {
    Write-Host "✗ Failed to login user" -ForegroundColor Red
    exit 1
}

$USER_TOKEN = $userLoginData.token
Write-Host "✓ User logged in successfully" -ForegroundColor Green
Write-Host "  Token: $($USER_TOKEN.Substring(0, 20))..." -ForegroundColor Cyan

# --------

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "STEP 4: Driver Login" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan

$driverLoginBody = @{
    username = $DRIVER_USERNAME
    password = $DRIVER_PASSWORD
} | ConvertTo-Json

$driverLoginResponse = curl -s -X POST "$BASE_URL/api/auth/login" `
    -H "Content-Type: application/json" `
    -d $driverLoginBody

Write-Host "Response: $driverLoginResponse" -ForegroundColor Green
$driverLoginData = $driverLoginResponse | ConvertFrom-Json -ErrorAction SilentlyContinue

if ($null -eq $driverLoginData.token) {
    Write-Host "✗ Failed to login driver" -ForegroundColor Red
    exit 1
}

$DRIVER_TOKEN = $driverLoginData.token
Write-Host "✓ Driver logged in successfully" -ForegroundColor Green
Write-Host "  Token: $($DRIVER_TOKEN.Substring(0, 20))..." -ForegroundColor Cyan

# --------

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "STEP 5: User Creates a Ride" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan

$createRideBody = @{
    pickupLocation = "Downtown Station"
    dropLocation = "Airport Terminal 2"
} | ConvertTo-Json

$createRideResponse = curl -s -X POST "$BASE_URL/api/v1/rides" `
    -H "Content-Type: application/json" `
    -H "Authorization: Bearer $USER_TOKEN" `
    -d $createRideBody

Write-Host "Response: $createRideResponse" -ForegroundColor Green
$rideData = $createRideResponse | ConvertFrom-Json -ErrorAction SilentlyContinue

if ($null -eq $rideData.id) {
    Write-Host "✗ Failed to create ride" -ForegroundColor Red
    exit 1
}

$RIDE_ID = $rideData.id
Write-Host "✓ Ride created successfully" -ForegroundColor Green
Write-Host "  Ride ID: $RIDE_ID" -ForegroundColor Cyan
Write-Host "  Pickup: $($rideData.pickupLocation)" -ForegroundColor Cyan
Write-Host "  Dropoff: $($rideData.dropLocation)" -ForegroundColor Cyan
Write-Host "  Status: $($rideData.status)" -ForegroundColor Cyan

# --------

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "STEP 6: Driver Views Pending Requests" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan

$viewRequestsResponse = curl -s -X GET "$BASE_URL/api/v1/driver/rides/requests" `
    -H "Authorization: Bearer $DRIVER_TOKEN"

Write-Host "Response: $viewRequestsResponse" -ForegroundColor Green
$requestsData = $viewRequestsResponse | ConvertFrom-Json -ErrorAction SilentlyContinue

if ($requestsData -is [System.Object[]]) {
    Write-Host "✓ Driver retrieved pending requests" -ForegroundColor Green
    Write-Host "  Total requests: $($requestsData.Count)" -ForegroundColor Cyan
    
    if ($requestsData.Count -gt 0) {
        foreach ($request in $requestsData) {
            Write-Host "  - Ride ID: $($request.id), Pickup: $($request.pickupLocation), Status: $($request.status)" -ForegroundColor Gray
        }
    }
} else {
    Write-Host "✗ Invalid response format for pending requests" -ForegroundColor Red
    exit 1
}

# --------

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "STEP 7: Driver Accepts the Ride" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan

$acceptRideResponse = curl -s -X POST "$BASE_URL/api/v1/driver/rides/$RIDE_ID/accept" `
    -H "Authorization: Bearer $DRIVER_TOKEN"

Write-Host "Response: $acceptRideResponse" -ForegroundColor Green
$acceptData = $acceptRideResponse | ConvertFrom-Json -ErrorAction SilentlyContinue

if ($null -eq $acceptData.id) {
    Write-Host "✗ Failed to accept ride" -ForegroundColor Red
    exit 1
}

Write-Host "✓ Driver accepted the ride" -ForegroundColor Green
Write-Host "  Ride ID: $($acceptData.id)" -ForegroundColor Cyan
Write-Host "  Status: $($acceptData.status)" -ForegroundColor Cyan
Write-Host "  Driver ID: $($acceptData.driverId)" -ForegroundColor Cyan

# --------

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "STEP 8: Complete the Ride" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan

$completeRideResponse = curl -s -X POST "$BASE_URL/api/v1/rides/$RIDE_ID/complete" `
    -H "Authorization: Bearer $DRIVER_TOKEN"

Write-Host "Response: $completeRideResponse" -ForegroundColor Green
$completeData = $completeRideResponse | ConvertFrom-Json -ErrorAction SilentlyContinue

if ($null -eq $completeData.id) {
    Write-Host "✗ Failed to complete ride" -ForegroundColor Red
    exit 1
}

Write-Host "✓ Ride completed successfully" -ForegroundColor Green
Write-Host "  Ride ID: $($completeData.id)" -ForegroundColor Cyan
Write-Host "  Final Status: $($completeData.status)" -ForegroundColor Cyan

# --------

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "SUMMARY: COMPLETE DRIVER WORKFLOW" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "✓ Driver registered: $DRIVER_USERNAME" -ForegroundColor Green
Write-Host "✓ User registered: $USER_USERNAME" -ForegroundColor Green
Write-Host "✓ User created ride (ID: $RIDE_ID)" -ForegroundColor Green
Write-Host "✓ Driver viewed pending requests" -ForegroundColor Green
Write-Host "✓ Driver accepted ride (Status: $($acceptData.status))" -ForegroundColor Green
Write-Host "✓ Ride completed (Status: $($completeData.status))" -ForegroundColor Green
Write-Host "`n✓✓✓ COMPLETE DRIVER WORKFLOW SUCCESSFUL ✓✓✓" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan
