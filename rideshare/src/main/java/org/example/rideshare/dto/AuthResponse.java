package org.example.rideshare.dto;

/**
 * AuthResponse - Data Transfer Object for authentication responses
 * 
 * This DTO encapsulates the JWT bearer token returned after successful
 * registration or login. The token must be included in the Authorization
 * header (Bearer token) for all subsequent API requests to protected endpoints.
 * 
 * Token Format: "Bearer {jwt_token}"
 * Token Validity: 1 hour (3600000 milliseconds)
 * 
 * Used by: AuthController.register(), AuthController.login()
 */
public class AuthResponse {
    // JWT bearer token for authentication
    private String token;

    // Default constructor for deserialization
    public AuthResponse() {
    }

    // Constructor with token initialization
    public AuthResponse(String token) {
        this.token = token;
    }

    // Getter for JWT token
    public String getToken() {
        return token;
    }

    // Setter for JWT token
    public void setToken(String token) {
        this.token = token;
    }
}
