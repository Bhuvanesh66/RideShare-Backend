package org.example.rideshare.service;

import org.example.rideshare.dto.AuthResponse;
import org.example.rideshare.dto.LoginRequest;
import org.example.rideshare.dto.RegisterRequest;

/**
 * AuthService - Service interface for user authentication and registration
 * 
 * This interface defines the contract for authentication operations including
 * user registration and login. Implementations handle password hashing,
 * JWT token generation, and user persistence.
 * 
 * Implemented by: AuthServiceImpl
 */
public interface AuthService {
    /**
     * Register a new user
     * 
     * Creates a new user account with the provided credentials and role.
     * Password is hashed using BCrypt before storage.
     * 
     * @param request RegisterRequest containing username, password, and role
     * @return AuthResponse with JWT bearer token
     * @throws RuntimeException if username already exists
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Authenticate a user and return JWT token
     * 
     * Verifies the provided credentials against stored user data.
     * If valid, generates and returns a JWT token for subsequent API requests.
     * 
     * @param request LoginRequest containing username and password
     * @return AuthResponse with JWT bearer token
     * @throws NotFoundException if user not found or credentials invalid
     */
    AuthResponse login(LoginRequest request);
}
