package org.example.rideshare.controller;

import jakarta.validation.Valid;
import org.example.rideshare.dto.CreateRideRequest;
import org.example.rideshare.model.Ride;
import org.example.rideshare.model.User;
import org.example.rideshare.repository.UserRepository;
import org.example.rideshare.service.RideService;
import org.example.rideshare.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * RideController - Handles all ride-related HTTP requests
 * 
 * Purpose: Provides endpoints for creating, viewing, accepting, and completing
 * rides
 * 
 * Endpoints:
 * - POST /api/v1/rides : Create ride (USER only)
 * - GET /api/v1/user/rides : View my rides (USER only)
 * - GET /api/v1/driver/rides/requests : View pending rides (DRIVER only)
 * - POST /api/v1/driver/rides/{id}/accept : Accept ride (DRIVER only)
 * - POST /api/v1/rides/{id}/complete : Complete ride (USER/DRIVER)
 * 
 * Security: All endpoints require valid JWT Bearer token in Authorization
 * header
 * Role-Based Access: Uses @PreAuthorize to restrict access based on user role
 * 
 * Authorization Flow:
 * 1. JWT token extracted from Authorization header by JwtAuthFilter
 * 2. Token signature validated using secret key
 * 3. Token expiration checked
 * 4. User role extracted from token claims
 * 5. @PreAuthorize evaluates role requirements
 * 6. Request allowed or rejected based on role
 * 
 * Author: RideShare Development Team
 * Version: 1.0
 */
@RestController
@RequestMapping("/api/v1")
public class RideController {

    @Autowired
    private RideService rideService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Create a new ride request
     * 
     * Endpoint: POST /api/v1/rides
     * Access: USER only (requires ROLE_USER)
     * Security: Requires valid JWT Bearer token
     * 
     * @param request CreateRideRequest containing:
     *                - pickupLocation (required, non-blank)
     *                - dropLocation (required, non-blank)
     * 
     *                Process:
     *                1. Extract current user from JWT token using SecurityUtil
     *                2. Retrieve User object from database by username
     *                3. Create ride with userId, default status "REQUESTED", null
     *                driverId
     *                4. Set createdAt timestamp
     *                5. Save ride to MongoDB
     *                6. Return created ride object
     * 
     * @return ResponseEntity with status 201 (Created) and Ride object
     *         Ride object includes: id, userId, driverId (null), pickupLocation,
     *         dropLocation,
     *         status ("REQUESTED"), createdAt timestamp
     * 
     *         Example Request:
     *         POST /api/v1/rides
     *         Authorization: Bearer <JWT_TOKEN>
     *         Content-Type: application/json
     * 
     *         {
     *         "pickupLocation": "Central Station",
     *         "dropLocation": "Airport Terminal 1"
     *         }
     * 
     *         Example Response (201):
     *         {
     *         "id": "507f1f77bcf86cd799439011",
     *         "userId": "507f1f77bcf86cd799439010",
     *         "driverId": null,
     *         "pickupLocation": "Central Station",
     *         "dropLocation": "Airport Terminal 1",
     *         "status": "REQUESTED",
     *         "createdAt": "2024-01-15T10:30:00Z"
     *         }
     * 
     *         Errors:
     *         - 400 Bad Request: Invalid input (pickup/drop blank)
     *         - 401 Unauthorized: Missing or invalid JWT token
     *         - 403 Forbidden: User lacks ROLE_USER authority
     */
    @PostMapping("/rides")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<Ride> createRide(@Valid @RequestBody CreateRideRequest request) {
        // Extract current authenticated username from security context
        String username = SecurityUtil.getCurrentUsername();
        // Retrieve user from database (guaranteed to exist due to authentication)
        User user = userRepository.findByUsername(username).orElseThrow();
        // Delegate to service layer to create ride with user's ID
        Ride ride = rideService.createRide(request, user.getId());
        // Return 201 Created with created ride object
        return ResponseEntity.ok(ride);
    }

    /**
     * Retrieve all rides created by current user
     * 
     * Endpoint: GET /api/v1/user/rides
     * Access: USER only (requires ROLE_USER)
     * Security: Requires valid JWT Bearer token
     * 
     * Process:
     * 1. Extract current user from JWT token using SecurityUtil
     * 2. Retrieve User object from database by username
     * 3. Query all rides where userId matches current user
     * 4. Return list of rides (could be empty)
     * 
     * @return ResponseEntity with status 200 (OK) and List<Ride>
     *         Returns all rides created by user regardless of status
     *         (REQUESTED, ACCEPTED, COMPLETED)
     * 
     *         Example Request:
     *         GET /api/v1/user/rides
     *         Authorization: Bearer <JWT_TOKEN>
     * 
     *         Example Response (200):
     *         [
     *         {
     *         "id": "507f1f77bcf86cd799439011",
     *         "userId": "507f1f77bcf86cd799439010",
     *         "driverId": "507f1f77bcf86cd799439012",
     *         "pickupLocation": "Central Station",
     *         "dropLocation": "Airport Terminal 1",
     *         "status": "ACCEPTED",
     *         "createdAt": "2024-01-15T10:30:00Z"
     *         },
     *         {
     *         "id": "507f1f77bcf86cd799439013",
     *         "userId": "507f1f77bcf86cd799439010",
     *         "driverId": null,
     *         "pickupLocation": "Hotel Downtown",
     *         "dropLocation": "Train Station",
     *         "status": "REQUESTED",
     *         "createdAt": "2024-01-15T11:00:00Z"
     *         }
     *         ]
     * 
     *         Errors:
     *         - 401 Unauthorized: Missing or invalid JWT token
     *         - 403 Forbidden: User lacks ROLE_USER authority
     */
    @GetMapping("/user/rides")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<List<Ride>> getMyRides() {
        // Extract current authenticated username from security context
        String username = SecurityUtil.getCurrentUsername();
        // Retrieve user from database (guaranteed to exist due to authentication)
        User user = userRepository.findByUsername(username).orElseThrow();
        // Get all rides for this user and return
        return ResponseEntity.ok(rideService.getUserRides(user.getId()));
    }

    /**
     * Retrieve all pending ride requests (unaccepted rides)
     * 
     * Endpoint: GET /api/v1/driver/rides/requests
     * Access: DRIVER only (requires ROLE_DRIVER)
     * Security: Requires valid JWT Bearer token
     * 
     * Purpose: Allow drivers to see all ride requests available to accept
     * A ride is "pending" when status = "REQUESTED" and driverId = null
     * 
     * Process:
     * 1. Query all rides with status = "REQUESTED"
     * 2. Return list of rides available for driver to accept
     * 
     * @return ResponseEntity with status 200 (OK) and List<Ride>
     *         Returns all rides with status "REQUESTED"
     *         Ordered by creation time (oldest first)
     * 
     *         Example Request:
     *         GET /api/v1/driver/rides/requests
     *         Authorization: Bearer <JWT_TOKEN>
     * 
     *         Example Response (200):
     *         [
     *         {
     *         "id": "507f1f77bcf86cd799439011",
     *         "userId": "507f1f77bcf86cd799439010",
     *         "driverId": null,
     *         "pickupLocation": "Central Station",
     *         "dropLocation": "Airport Terminal 1",
     *         "status": "REQUESTED",
     *         "createdAt": "2024-01-15T10:30:00Z"
     *         }
     *         ]
     * 
     *         Errors:
     *         - 401 Unauthorized: Missing or invalid JWT token
     *         - 403 Forbidden: User lacks ROLE_DRIVER authority
     */
    @GetMapping("/driver/rides/requests")
    @PreAuthorize("hasAuthority('ROLE_DRIVER')")
    public ResponseEntity<List<Ride>> getPendingRides() {
        // Retrieve and return all pending rides from service layer
        return ResponseEntity.ok(rideService.getPendingRides());
    }

    /**
     * Accept a pending ride request
     * 
     * Endpoint: POST /api/v1/driver/rides/{rideId}/accept
     * Access: DRIVER only (requires ROLE_DRIVER)
     * Security: Requires valid JWT Bearer token
     * 
     * @param rideId MongoDB ObjectId of the ride to accept (path parameter)
     *               Must be a valid MongoDB ObjectId (24 hex characters)
     * 
     *               Process:
     *               1. Extract current driver from JWT token using SecurityUtil
     *               2. Retrieve Driver User object from database by username
     *               3. Find ride by rideId
     *               4. Verify ride exists and status is "REQUESTED"
     *               5. Update ride: set driverId, change status to "ACCEPTED"
     *               6. Save updated ride to MongoDB
     *               7. Return updated ride object
     * 
     * @return ResponseEntity with status 200 (OK) and updated Ride object
     *         Ride object now includes: driverId assigned, status = "ACCEPTED"
     * 
     *         Example Request:
     *         POST /api/v1/driver/rides/507f1f77bcf86cd799439011/accept
     *         Authorization: Bearer <JWT_TOKEN>
     * 
     *         Example Response (200):
     *         {
     *         "id": "507f1f77bcf86cd799439011",
     *         "userId": "507f1f77bcf86cd799439010",
     *         "driverId": "507f1f77bcf86cd799439012",
     *         "pickupLocation": "Central Station",
     *         "dropLocation": "Airport Terminal 1",
     *         "status": "ACCEPTED",
     *         "createdAt": "2024-01-15T10:30:00Z"
     *         }
     * 
     *         Errors:
     *         - 400 Bad Request: Invalid ride ID format
     *         - 401 Unauthorized: Missing or invalid JWT token
     *         - 403 Forbidden: User lacks ROLE_DRIVER authority
     *         - 404 Not Found: Ride does not exist
     *         - 409 Conflict: Ride already accepted by another driver or completed
     */
    @PostMapping("/driver/rides/{rideId}/accept")
    @PreAuthorize("hasAuthority('ROLE_DRIVER')")
    public ResponseEntity<Ride> acceptRide(@PathVariable String rideId) {
        // Extract current authenticated driver from security context
        String username = SecurityUtil.getCurrentUsername();
        // Retrieve driver user from database (guaranteed to exist due to
        // authentication)
        User driver = userRepository.findByUsername(username).orElseThrow();
        // Accept ride with driver's ID and return updated ride
        return ResponseEntity.ok(rideService.acceptRide(rideId, driver.getId()));
    }

    /**
     * Complete a ride
     * 
     * Endpoint: POST /api/v1/rides/{rideId}/complete
     * Access: USER or DRIVER (requires either ROLE_USER or ROLE_DRIVER)
     * Security: Requires valid JWT Bearer token
     * 
     * @param rideId MongoDB ObjectId of the ride to complete (path parameter)
     *               Must be a valid MongoDB ObjectId (24 hex characters)
     * 
     *               Purpose: Mark a ride as completed
     *               Available to both USER (ride requester) and DRIVER (ride
     *               acceptor)
     * 
     *               Process:
     *               1. Find ride by rideId
     *               2. Verify ride exists and status is "ACCEPTED"
     *               3. Update ride: change status to "COMPLETED"
     *               4. Save updated ride to MongoDB
     *               5. Return updated ride object
     * 
     * @return ResponseEntity with status 200 (OK) and updated Ride object
     *         Ride object now has: status = "COMPLETED"
     *         All other fields remain unchanged
     * 
     *         Example Request:
     *         POST /api/v1/rides/507f1f77bcf86cd799439011/complete
     *         Authorization: Bearer <JWT_TOKEN>
     * 
     *         Example Response (200):
     *         {
     *         "id": "507f1f77bcf86cd799439011",
     *         "userId": "507f1f77bcf86cd799439010",
     *         "driverId": "507f1f77bcf86cd799439012",
     *         "pickupLocation": "Central Station",
     *         "dropLocation": "Airport Terminal 1",
     *         "status": "COMPLETED",
     *         "createdAt": "2024-01-15T10:30:00Z"
     *         }
     * 
     *         Errors:
     *         - 400 Bad Request: Invalid ride ID format
     *         - 401 Unauthorized: Missing or invalid JWT token
     *         - 404 Not Found: Ride does not exist
     *         - 409 Conflict: Ride not in ACCEPTED status (already completed or
     *         still requested)
     */
    @PostMapping("/rides/{rideId}/complete")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_DRIVER')")
    public ResponseEntity<Ride> completeRide(@PathVariable String rideId) {
        // Complete ride and return updated ride object from service layer
        return ResponseEntity.ok(rideService.completeRide(rideId));
    }
}
