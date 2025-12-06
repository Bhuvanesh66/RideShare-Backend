package org.example.rideshare.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * LoginRequest - Data Transfer Object for user authentication
 * 
 * This DTO encapsulates the credentials required for user login.
 * Both username and password fields are required (non-blank).
 * 
 * Used by: AuthController.login()
 * 
 * Response: Returns AuthResponse with JWT bearer token
 */
public class LoginRequest {

    // Username of the user attempting to login
    @NotBlank
    private String username;

    // Password for authentication (will be verified against BCrypt hash)
    @NotBlank
    private String password;

    // Default constructor for deserialization
    public LoginRequest() {
    }

    // Getter for username
    public String getUsername() {
        return username;
    }

    // Setter for username
    public void setUsername(String username) {
        this.username = username;
    }

    // Getter for password
    public String getPassword() {
        return password;
    }

    // Setter for password
    public void setPassword(String password) {
        this.password = password;
    }
}
