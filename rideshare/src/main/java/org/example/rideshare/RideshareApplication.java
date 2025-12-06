package org.example.rideshare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * RideshareApplication - Main entry point for the RideShare application
 * 
 * This is the Spring Boot application bootstrap class that initializes and
 * starts
 * the RideShare backend server. The @SpringBootApplication annotation provides:
 * - @SpringBootConfiguration: Marks this as a Spring configuration class
 * - @EnableAutoConfiguration: Enables Spring Boot's auto-configuration
 * - @ComponentScan: Scans for components, services, and repositories
 * 
 * Application Overview:
 * RideShare is a ride-sharing platform backend that provides:
 * - User authentication and registration (supports ROLE_USER and ROLE_DRIVER)
 * - Ride management (create, view, accept, complete rides)
 * - User profiles and driver information
 * - JWT-based security for all API endpoints
 * 
 * Technologies:
 * - Spring Boot: Web framework and auto-configuration
 * - Spring Security: JWT authentication and authorization
 * - Spring Data JPA: Database persistence
 * - MySQL: Data storage
 * - JWT: Stateless authentication tokens
 * 
 * Main endpoints available:
 * - POST /api/auth/register - User registration
 * - POST /api/auth/login - User authentication
 * - POST /api/rides - Create a new ride request
 * - GET /api/rides - View available rides
 * - PUT /api/rides/{id}/accept - Accept a ride as driver
 * - PUT /api/rides/{id}/complete - Complete a ride
 * - GET /api/profile - Get user profile
 * 
 * @author RideShare Development Team
 * @version 1.0
 */
@SpringBootApplication
public class RideshareApplication {

    /**
     * Main entry point for the Spring Boot application
     * 
     * Initializes and starts the embedded Tomcat server on the configured port
     * (default: 8080). The application loads configuration from
     * application.properties
     * and performs component scanning for Spring-managed beans.
     * 
     * @param args Command line arguments (typically empty in production)
     */
    public static void main(String[] args) {
        SpringApplication.run(RideshareApplication.class, args);
    }
}
