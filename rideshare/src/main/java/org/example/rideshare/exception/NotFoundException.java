package org.example.rideshare.exception;

/**
 * NotFoundException - Custom exception for resource not found errors
 * 
 * This is a runtime exception thrown when a requested resource cannot be found
 * in the database. It is caught by the GlobalExceptionHandler and returns a
 * 404 (Not Found) HTTP response to the client.
 * 
 * Typical scenarios:
 * - User ID not found in database
 * - Ride ID not found in database
 * 
 * Used by: Service layer methods (AuthService, RideService)
 * Handled by: GlobalExceptionHandler
 */
public class NotFoundException extends RuntimeException {
    // Constructor that accepts an error message
    public NotFoundException(String message) {
        super(message);
    }
}
