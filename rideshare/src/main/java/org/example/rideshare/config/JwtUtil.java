/**
 * JwtUtil - JWT (JSON Web Token) generation and validation utility
 * 
 * This component handles all JWT operations for the RideShare application:
 * - Token generation: Creates signed JWT tokens for authenticated users
 * - Token validation: Verifies token signatures and expiration
 * - Claims extraction: Retrieves username and role from tokens
 * 
 * Security Details:
 * - Algorithm: HMAC-SHA256 for token signing
 * - Secret: Loaded from application.properties (app.jwt.secret)
 * - Expiration: Configured in application.properties (app.jwt.expiration-ms)
 * - Claims: Includes username (subject) and role as custom claim
 * 
 * Configuration Properties:
 * - app.jwt.secret: Secret key for token signing (should be >256 bits)
 * - app.jwt.expiration-ms: Token validity duration in milliseconds
 * 
 * Used by: JwtAuthFilter for token validation in request processing
 * 
 * @author RideShare Development Team
 * @version 1.0
 */
package org.example.rideshare.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // Cryptographic key for HMAC-SHA256 signing
    private final Key key;

    // Token validity duration in milliseconds
    private final long expirationMs;

    /**
     * Constructor - Initializes JWT utility with secret key and expiration
     * 
     * Constructs the HMAC-SHA256 key from the configured secret string.
     * The secret must be at least 32 characters (256 bits) for HS256.
     * 
     * @param secret       The secret key string from application.properties
     * @param expirationMs Token validity duration in milliseconds
     */
    /**
     * Generate a new JWT token for a user
     * 
     * Creates a digitally signed JWT token containing the username and role.
     * The token can be used in subsequent API requests for authentication.
     * 
     * Token Claims:
     * - subject (sub): Username of the authenticated user
     * - role: User role (ROLE_USER or ROLE_DRIVER)
     * - iat (issuedAt): Token generation timestamp
     * - exp (expiration): Token expiration timestamp
     * 
     * @param username The username to embed in the token
     * @param role     The user role to embed in the token
     * @return Signed JWT token as a compact serialized string
     */
    /**
     * Extract username from a JWT token
     * 
     * Parses and validates the token, then extracts the subject claim
     * which contains the username.
     * 
     * @param token The JWT token to parse
     * @return The username embedded in the token
     */
    /**
     * Extract role from a JWT token
     * 
     * Parses and validates the token, then extracts the custom role claim.
     * 
     * @param token The JWT token to parse
     * @return The role embedded in the token, or null if not present
     */
    /**
     * Validate a JWT token
     * 
     * Verifies the token signature and checks expiration.
     * Returns false if the token is invalid, expired, or malformed.
     * 
     * @param token The JWT token to validate
     * @return true if token is valid, false otherwise
     */
    /**
     * Parse and verify JWT token claims
     * 
     * Verifies the token signature using the stored key and extracts
     * the claims payload. This is an internal utility method used by
     * public methods to parse tokens.
     * 
     * @param token The JWT token to parse
     * @return The claims payload of the token
     * @throws JwtException if token signature is invalid or expired
     */

    public JwtUtil(@Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMs = expirationMs;
    }

    public String generateToken(String username, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public String getRole(String token) {
        Object role = parseClaims(token).get("role");
        return role != null ? role.toString() : null;
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return (Claims) Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
