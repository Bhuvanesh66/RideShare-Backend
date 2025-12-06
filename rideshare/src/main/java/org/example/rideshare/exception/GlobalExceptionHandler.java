package org.example.rideshare.exception;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * GlobalExceptionHandler - Centralized exception handling for the RideShare API
 * 
 * This class handles all exceptions thrown by controllers and services,
 * converting them into appropriate HTTP responses with consistent error format.
 * 
 * The @RestControllerAdvice annotation makes this a global exception handler
 * that applies to all REST controllers in the application.
 * 
 * Handled Exceptions:
 * 1. NotFoundException - Returns 404 (NOT_FOUND)
 * 2. MethodArgumentNotValidException - Returns 400 (VALIDATION_ERROR)
 * 3. IllegalStateException - Returns 400 (BAD_REQUEST)
 * 
 * Error Response Format:
 * {
 * "error": "ERROR_TYPE",
 * "message": "Error description",
 * "timestamp": "ISO 8601 timestamp"
 * }
 * 
 * @author RideShare Development Team
 * @version 1.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle NotFoundException - Resource not found (404)
     * 
     * Triggered when a requested resource (user, ride, etc.) cannot be found
     * in the database. Returns HTTP 404 with error details.
     * 
     * @param ex The NotFoundException containing the error message
     * @return ResponseEntity with error details and 404 status
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NotFoundException ex) {
        // Create error response body
        Map<String, Object> body = new HashMap<>();
        body.put("error", "NOT_FOUND");
        body.put("message", ex.getMessage());
        body.put("timestamp", Instant.now().toString());
        // Return 404 response
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * Handle MethodArgumentNotValidException - Validation failed (400)
     * 
     * Triggered when request body validation fails (e.g., missing required fields,
     * invalid format). Extracts the first validation error and returns it to the
     * client.
     * 
     * @param ex The MethodArgumentNotValidException containing validation errors
     * @return ResponseEntity with validation error details and 400 status
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        // Create error response body
        Map<String, Object> body = new HashMap<>();
        body.put("error", "VALIDATION_ERROR");
        body.put("timestamp", Instant.now().toString());

        // Extract first validation error message
        String message = ex.getBindingResult().getFieldErrors()
                .stream()
                .findFirst()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .orElse("Validation error");
        body.put("message", message);

        // Return 400 response with validation error
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Handle IllegalStateException - Invalid business logic state (400)
     * 
     * Triggered when an operation is attempted in an invalid state
     * (e.g., trying to accept a ride that's already completed).
     * 
     * @param ex The IllegalStateException containing the error message
     * @return ResponseEntity with error details and 400 status
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException ex) {
        // Create error response body
        Map<String, Object> body = new HashMap<>();
        body.put("error", "BAD_REQUEST");
        body.put("message", ex.getMessage());
        body.put("timestamp", Instant.now().toString());
        // Return 400 response
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
