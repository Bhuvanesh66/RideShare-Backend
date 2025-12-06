package org.example.rideshare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

/**
 * RegisterRequest - Data Transfer Object for user registration
 * 
 * This DTO encapsulates the data required for user registration.
 * All fields are validated using Jakarta validation annotations.
 * 
 * Validation Rules:
 * - username: Must not be blank and must be at least 3 characters
 * - password: Must not be blank and must be at least 4 characters
 * - role: Must be either "ROLE_USER" or "ROLE_DRIVER"
 * 
 * Used by: AuthController.register()
 */
public class RegisterRequest {

    // Username for the new user account - must be unique and at least 3 characters
    @NotBlank
    @Size(min = 3, message = "Username must be at least 3 characters")
    private String username;

    // Password for the user account - must be at least 4 characters (will be BCrypt
    // encoded)
    @NotBlank
    @Size(min = 4, message = "Password must be at least 4 characters")
    private String password;

    // User role - determines access level (ROLE_USER for passengers, ROLE_DRIVER
    // for drivers)
    @NotBlank(message = "Role is required")
    @Pattern(regexp = "ROLE_USER|ROLE_DRIVER", message = "Role must be either ROLE_USER or ROLE_DRIVER")
    private String role;

    // Default constructor for deserialization
    public RegisterRequest() {
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

    // Getter for role
    public String getRole() {
        return role;
    }

    // Setter for role
    public void setRole(String role) {
        this.role = role;
    }
}
