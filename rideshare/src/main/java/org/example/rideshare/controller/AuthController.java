package org.example.rideshare.controller;

import jakarta.validation.Valid;
import org.example.rideshare.dto.AuthResponse;
import org.example.rideshare.dto.LoginRequest;
import org.example.rideshare.dto.RegisterRequest;
import org.example.rideshare.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController - Handles all authentication-related HTTP requests
 * 
 * Purpose: Provides public endpoints for user registration and login
 * 
 * Endpoints:
 * - POST /api/auth/register : Create new user account with JWT token (PUBLIC)
 * - POST /api/auth/login : Authenticate user and return JWT token (PUBLIC)
 * 
 * Security: These endpoints are publicly accessible (no authentication
 * required)
 * Response: Returns JWT Bearer token which must be included in Authorization
 * header
 * for all protected endpoints
 * 
 * Error Handling:
 * - 400 Bad Request: Invalid input data (validation failure, duplicate
 * username)
 * - 401 Unauthorized: Invalid credentials during login
 * - 409 Conflict: Username already exists
 * 
 * Author: RideShare Development Team
 * Version: 1.0
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Register a new user
     * 
     * Endpoint: POST /api/auth/register
     * Access: PUBLIC (no authentication required)
     * 
     * @param request RegisterRequest containing:
     *                - username (min 3 chars, must be unique)
     *                - password (min 4 chars)
     *                - role (ROLE_USER or ROLE_DRIVER only)
     * 
     *                Process:
     *                1. Validate input using @Valid annotation (username size, role
     *                pattern, etc.)
     *                2. Check for duplicate username in database
     *                3. Hash password using BCryptPasswordEncoder
     *                4. Save user to MongoDB
     *                5. Generate JWT token using JwtUtil (expires in 1 hour)
     *                6. Return token in response body
     * 
     * @return ResponseEntity with status 201 (Created) and AuthResponse containing
     *         JWT token
     *         On error: 400 (Bad Request) with error message
     * 
     *         Example Request:
     *         {
     *         "username": "john",
     *         "password": "1234",
     *         "role": "ROLE_USER"
     *         }
     * 
     *         Example Response (201):
     *         {
     *         "token":
     *         "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huIiwicm9sZSI6IlJPTEVfVVNFUiIsImlhdCI6MTcwMTk1MjAwMCwiZXhwIjoxNzAxOTU1NjAwfQ.abc123..."
     *         }
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            // Delegate to service layer which handles business logic
            AuthResponse response = authService.register(request);
            // Return 201 Created status with JWT token in response body
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            // Handle validation errors (e.g., duplicate username)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Login a user
     * 
     * Endpoint: POST /api/auth/login
     * Access: PUBLIC (no authentication required)
     * 
     * @param request LoginRequest containing:
     *                - username (registered username)
     *                - password (plaintext password, will be verified against
     *                BCrypt hash)
     * 
     *                Process:
     *                1. Validate input using @Valid annotation
     *                2. Retrieve user from database by username
     *                3. Verify password using BCryptPasswordEncoder.matches()
     *                4. Authenticate using Spring Security's AuthenticationManager
     *                5. Generate new JWT token if authentication successful
     *                6. Return token in response body
     * 
     * @return ResponseEntity with status 200 (OK) and AuthResponse containing JWT
     *         token
     *         On error: 401 (Unauthorized) if credentials are invalid
     * 
     *         Example Request:
     *         {
     *         "username": "john",
     *         "password": "1234"
     *         }
     * 
     *         Example Response (200):
     *         {
     *         "token":
     *         "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huIiwicm9sZSI6IlJPTEVfVVNFUiIsImlhdCI6MTcwMTk1MjAwMCwiZXhwIjoxNzAxOTU1NjAwfQ.abc123..."
     *         }
     * 
     *         Token Usage:
     *         Include in Authorization header for protected endpoints:
     *         Authorization: Bearer {token}
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * ErrorResponse - Inner class for consistent error message format
     * 
     * Used to return structured error responses with HTTP 400/401 status codes
     * Ensures consistent error format across all endpoints
     * 
     * Example response:
     * {
     * "message": "Username already exists"
     * }
     */
    static class ErrorResponse {
        public String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
