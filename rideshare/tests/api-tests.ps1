# Comprehensive RideShare API Test Script
# This script tests all 7 endpoints of the RideShare API
# Purpose: Validate authentication, authorization, role-based access, and complete ride workflow
# 
# Features:
# - Tests all endpoints with correct and incorrect scenarios
# - Validates response status codes and data
# - Color-coded output for easy reading
# - Automatic token extraction for subsequent requests
# - Comprehensive error handling and reporting
#
# Author: RideShare Development Team
# Version: 1.0

# Configuration
$baseUrl = "http://localhost:8081"
$contentType = "application/json"

# Generate unique usernames for each test run to avoid conflicts
$timestamp = Get-Date -Format "yyyyMMddHHmmss"
$testUser = "user_$timestamp"
$testDriver = "driver_$timestamp"

# Global variables to store tokens for subsequent tests
$userToken = $null
$driverToken = $null
$rideId = $null

# Test result tracking
$testsPassed = 0
$testsFailed = 0

Write-Host -ForegroundColor Cyan "RideShare API - Comprehensive Test Suite - All 7 Endpoints Tested"

# ==================================================================================
# TEST 1: Register a USER
# ==================================================================================
Write-Host -ForegroundColor Yellow "`n[TEST 1] Register USER"
Write-Host -ForegroundColor Yellow "Endpoint: POST /api/auth/register"
Write-Host -ForegroundColor Yellow "Purpose: Create a new USER account and receive JWT token"

$body = @{
    username = $testUser
    password = "1234"
    role = "ROLE_USER"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/auth/register" `
        -Method Post `
        -Headers @{"Content-Type" = $contentType} `
        -Body $body `
        -ErrorAction Stop

    if ($response.token) {
        $userToken = $response.token
        Write-Host -ForegroundColor Green "[PASS] User registered successfully"
        Write-Host -ForegroundColor Green "Token received: $($response.token.Substring(0, 50))..."
        $testsPassed++
    } else {
        Write-Host -ForegroundColor Red "[FAIL] No token in response"
        $testsFailed++
    }
} catch {
    Write-Host -ForegroundColor Red "[FAIL] $($_.Exception.Message)"
    $testsFailed++
}

# ==================================================================================
# TEST 2: Register a DRIVER
# ==================================================================================
Write-Host -ForegroundColor Yellow "`n[TEST 2] Register DRIVER"
Write-Host -ForegroundColor Yellow "Endpoint: POST /api/auth/register"
Write-Host -ForegroundColor Yellow "Purpose: Create a new DRIVER account and receive JWT token"

$body = @{
    username = $testDriver
    password = "abcd"
    role = "ROLE_DRIVER"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/auth/register" `
        -Method Post `
        -Headers @{"Content-Type" = $contentType} `
        -Body $body `
        -ErrorAction Stop

    if ($response.token) {
        $driverToken = $response.token
        Write-Host -ForegroundColor Green "[PASS] Driver registered successfully"
        Write-Host -ForegroundColor Green "Token received: $($response.token.Substring(0, 50))..."
        $testsPassed++
    } else {
        Write-Host -ForegroundColor Red "[FAIL] No token in response"
        $testsFailed++
    }
} catch {
    Write-Host -ForegroundColor Red "[FAIL] $($_.Exception.Message)"
    $testsFailed++
}

# ==================================================================================
# TEST 3: Duplicate Username Prevention
# ==================================================================================
Write-Host -ForegroundColor Yellow "`n[TEST 3] Duplicate Username Prevention"
Write-Host -ForegroundColor Yellow "Endpoint: POST /api/auth/register"
Write-Host -ForegroundColor Yellow "Purpose: Verify that duplicate usernames are rejected with 400 error"

$body = @{
    username = $testUser  # Same username as TEST 1
    password = "5678"
    role = "ROLE_USER"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/auth/register" `
        -Method Post `
        -Headers @{"Content-Type" = $contentType} `
        -Body $body `
        -ErrorAction Stop
    
    Write-Host -ForegroundColor Red "[FAIL] Duplicate username was accepted (should be rejected)"
    $testsFailed++
} catch {
    if ($_.Exception.Response.StatusCode -eq 400 -or $_.Exception.Response.StatusCode -eq 409) {
        Write-Host -ForegroundColor Green "[PASS] Duplicate username rejected with proper error"
        Write-Host -ForegroundColor Green "Error: $($_.Exception.Message)"
        $testsPassed++
    } else {
        Write-Host -ForegroundColor Red "[FAIL] Unexpected error: $($_.Exception.Message)"
        $testsFailed++
    }
}

# ==================================================================================
# TEST 4: Invalid Role Validation
# ==================================================================================
Write-Host -ForegroundColor Yellow "`n[TEST 4] Invalid Role Validation"
Write-Host -ForegroundColor Yellow "Endpoint: POST /api/auth/register"
Write-Host -ForegroundColor Yellow "Purpose: Verify that invalid roles are rejected with 400 error"

$body = @{
    username = "invalid_role_user"
    password = "1234"
    role = "ROLE_ADMIN"  # Invalid role (only ROLE_USER and ROLE_DRIVER allowed)
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/auth/register" `
        -Method Post `
        -Headers @{"Content-Type" = $contentType} `
        -Body $body `
        -ErrorAction Stop
    
    Write-Host -ForegroundColor Red "[FAIL] Invalid role was accepted (should be rejected)"
    $testsFailed++
} catch {
    if ($_.Exception.Response.StatusCode -eq 400) {
        Write-Host -ForegroundColor Green "[PASS] Invalid role rejected with 400 error"
        Write-Host -ForegroundColor Green "Error message received"
        $testsPassed++
    } else {
        Write-Host -ForegroundColor Red "[FAIL] Unexpected error: $($_.Exception.Message)"
        $testsFailed++
    }
}

# ==================================================================================
# TEST 5: Login
# ==================================================================================
Write-Host -ForegroundColor Yellow "`n[TEST 5] Login (Authentication)"
Write-Host -ForegroundColor Yellow "Endpoint: POST /api/auth/login"
Write-Host -ForegroundColor Yellow "Purpose: Authenticate user and receive new JWT token"

$body = @{
    username = $testUser
    password = "1234"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" `
        -Method Post `
        -Headers @{"Content-Type" = $contentType} `
        -Body $body `
        -ErrorAction Stop

    if ($response.token) {
        $userToken = $response.token  # Update token
        Write-Host -ForegroundColor Green "[PASS] User logged in successfully"
        Write-Host -ForegroundColor Green "New token received: $($response.token.Substring(0, 50))..."
        $testsPassed++
    } else {
        Write-Host -ForegroundColor Red "[FAIL] No token in response"
        $testsFailed++
    }
} catch {
    Write-Host -ForegroundColor Red "[FAIL] Login failed: $($_.Exception.Message)"
    $testsFailed++
}

# ==================================================================================
# TEST 6: Create Ride (USER ONLY - Protected Endpoint)
# ==================================================================================
Write-Host -ForegroundColor Yellow "`n[TEST 6] Create Ride (USER Authorization)"
Write-Host -ForegroundColor Yellow "Endpoint: POST /api/v1/rides"
Write-Host -ForegroundColor Yellow "Purpose: USER creates a new ride request (returns 201 Created)"
Write-Host -ForegroundColor Yellow "Security: Requires valid JWT Bearer token with ROLE_USER"

$body = @{
    pickupLocation = "Central Station"
    dropLocation = "Airport Terminal 1"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/v1/rides" `
        -Method Post `
        -Headers @{
            "Content-Type" = $contentType
            "Authorization" = "Bearer $userToken"
        } `
        -Body $body `
        -ErrorAction Stop

    if ($response.id) {
        $rideId = $response.id
        Write-Host -ForegroundColor Green "[PASS] Ride created successfully"
        Write-Host -ForegroundColor Green "Ride ID: $rideId"
        Write-Host -ForegroundColor Green "Status: $($response.status)"
        Write-Host -ForegroundColor Green "Pickup: $($response.pickupLocation)"
        Write-Host -ForegroundColor Green "Drop: $($response.dropLocation)"
        $testsPassed++
    } else {
        Write-Host -ForegroundColor Red "[FAIL] No ride ID in response"
        $testsFailed++
    }
} catch {
    Write-Host -ForegroundColor Red "[FAIL] $($_.Exception.Message)"
    $testsFailed++
}

# ==================================================================================
# TEST 7: Get My Rides (USER ONLY - Protected Endpoint)
# ==================================================================================
Write-Host -ForegroundColor Yellow "`n[TEST 7] Get My Rides (USER Authorization)"
Write-Host -ForegroundColor Yellow "Endpoint: GET /api/v1/user/rides"
Write-Host -ForegroundColor Yellow "Purpose: Retrieve all rides created by the current USER"
Write-Host -ForegroundColor Yellow "Security: Requires valid JWT Bearer token with ROLE_USER"

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/v1/user/rides" `
        -Method Get `
        -Headers @{
            "Content-Type" = $contentType
            "Authorization" = "Bearer $userToken"
        } `
        -ErrorAction Stop

    if ($response -is [Array]) {
        Write-Host -ForegroundColor Green "[PASS] Retrieved rides successfully"
        Write-Host -ForegroundColor Green "Total rides: $($response.Count)"
        if ($response.Count -gt 0) {
            Write-Host -ForegroundColor Green "Latest ride - ID: $($response[0].id), Status: $($response[0].status)"
        }
        $testsPassed++
    } else {
        Write-Host -ForegroundColor Red "[FAIL] Response is not an array"
        $testsFailed++
    }
} catch {
    Write-Host -ForegroundColor Red "[FAIL] $($_.Exception.Message)"
    $testsFailed++
}

# ==================================================================================
# TEST 8: Get Pending Rides (DRIVER ONLY - Protected Endpoint)
# ==================================================================================
Write-Host -ForegroundColor Yellow "`n[TEST 8] Get Pending Rides (DRIVER Authorization)"
Write-Host -ForegroundColor Yellow "Endpoint: GET /api/v1/driver/rides/requests"
Write-Host -ForegroundColor Yellow "Purpose: Retrieve all pending ride requests for DRIVER"
Write-Host -ForegroundColor Yellow "Security: Requires valid JWT Bearer token with ROLE_DRIVER"

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/v1/driver/rides/requests" `
        -Method Get `
        -Headers @{
            "Content-Type" = $contentType
            "Authorization" = "Bearer $driverToken"
        } `
        -ErrorAction Stop

    if ($response -is [Array]) {
        Write-Host -ForegroundColor Green "[PASS] Retrieved pending rides successfully"
        Write-Host -ForegroundColor Green "Pending rides count: $($response.Count)"
        if ($response.Count -gt 0) {
            Write-Host -ForegroundColor Green "First pending ride - ID: $($response[0].id), Pickup: $($response[0].pickupLocation)"
        }
        $testsPassed++
    } else {
        Write-Host -ForegroundColor Red "[FAIL] Response is not an array"
        $testsFailed++
    }
} catch {
    Write-Host -ForegroundColor Red "[FAIL] $($_.Exception.Message)"
    $testsFailed++
}

# ==================================================================================
# TEST 9: Accept Ride (DRIVER ONLY - Protected Endpoint)
# ==================================================================================
Write-Host -ForegroundColor Yellow "`n[TEST 9] Accept Ride (DRIVER Authorization)"
Write-Host -ForegroundColor Yellow "Endpoint: POST /api/v1/driver/rides/{rideId}/accept"
Write-Host -ForegroundColor Yellow "Purpose: DRIVER accepts a pending ride request (changes status to ACCEPTED)"
Write-Host -ForegroundColor Yellow "Security: Requires valid JWT Bearer token with ROLE_DRIVER"

if ($rideId) {
    try {
        $response = Invoke-RestMethod -Uri "$baseUrl/api/v1/driver/rides/$rideId/accept" `
            -Method Post `
            -Headers @{
                "Content-Type" = $contentType
                "Authorization" = "Bearer $driverToken"
            } `
            -ErrorAction Stop

        if ($response.status -eq "ACCEPTED" -and $response.driverId) {
            Write-Host -ForegroundColor Green "[PASS] Ride accepted successfully"
            Write-Host -ForegroundColor Green "Ride Status: $($response.status)"
            Write-Host -ForegroundColor Green "Driver ID assigned: $($response.driverId)"
            $testsPassed++
        } else {
            Write-Host -ForegroundColor Red "[FAIL] Ride status not updated to ACCEPTED"
            $testsFailed++
        }
    } catch {
        Write-Host -ForegroundColor Red "[FAIL] $($_.Exception.Message)"
        $testsFailed++
    }
} else {
    Write-Host -ForegroundColor Yellow "⊘ SKIP: No ride ID available (TEST 6 must pass first)"
}

# ==================================================================================
# TEST 10: Complete Ride (USER/DRIVER - Protected Endpoint)
# ==================================================================================
Write-Host -ForegroundColor Yellow "`n[TEST 10] Complete Ride (USER/DRIVER Authorization)"
Write-Host -ForegroundColor Yellow "Endpoint: POST /api/v1/rides/{rideId}/complete"
Write-Host -ForegroundColor Yellow "Purpose: Mark ride as COMPLETED (available to both USER and DRIVER)"
Write-Host -ForegroundColor Yellow "Security: Requires valid JWT Bearer token"

if ($rideId) {
    try {
        $response = Invoke-RestMethod -Uri "$baseUrl/api/v1/rides/$rideId/complete" `
            -Method Post `
            -Headers @{
                "Content-Type" = $contentType
                "Authorization" = "Bearer $userToken"
            } `
            -ErrorAction Stop

        if ($response.status -eq "COMPLETED") {
            Write-Host -ForegroundColor Green "[PASS] Ride completed successfully"
            Write-Host -ForegroundColor Green "Ride Status: $($response.status)"
            Write-Host -ForegroundColor Green "Journey: $($response.pickupLocation) -> $($response.dropLocation)"
            $testsPassed++
        } else {
            Write-Host -ForegroundColor Red "[FAIL] Ride status not updated to COMPLETED"
            $testsFailed++
        }
    } catch {
        Write-Host -ForegroundColor Red "[FAIL] $($_.Exception.Message)"
        $testsFailed++
    }
} else {
    Write-Host -ForegroundColor Yellow "⊘ SKIP: No ride ID available (TEST 6 must pass first)"
}

# ==================================================================================
# Test Summary
# ==================================================================================
Write-Host -ForegroundColor Cyan "TEST EXECUTION COMPLETE - Check results above"

$totalTests = $testsPassed + $testsFailed
$passPct = if ($totalTests -gt 0) { [math]::Round(($testsPassed / $totalTests) * 100) } else { 0 }

Write-Host ""
Write-Host -ForegroundColor Cyan "========== TEST SUMMARY =========="
Write-Host -ForegroundColor White "Total Tests: $totalTests"
Write-Host -ForegroundColor Green "Passed: $testsPassed"
Write-Host -ForegroundColor Red "Failed: $testsFailed"
Write-Host -ForegroundColor White "Success Rate: $passPct%"
Write-Host -ForegroundColor Cyan "=================================="`
Write-Host ""

if ($testsFailed -eq 0) {
    Write-Host -ForegroundColor Green "All endpoints tested successfully!"
} else {
    Write-Host -ForegroundColor Yellow "Some tests failed. Review errors above."
}
