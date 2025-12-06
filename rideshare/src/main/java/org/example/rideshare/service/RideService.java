package org.example.rideshare.service;

import org.example.rideshare.dto.CreateRideRequest;
import org.example.rideshare.model.Ride;

import java.util.List;

/**
 * RideService - Service interface for ride management operations
 * 
 * This interface defines the contract for ride-related business operations
 * including
 * creating ride requests, viewing rides, and managing ride lifecycle
 * transitions
 * (requesting, accepting, completing).
 * 
 * Ride Lifecycle:
 * 1. REQUESTED - Passenger creates a new ride request
 * 2. ACCEPTED - Driver accepts the pending ride request
 * 3. COMPLETED - Ride is completed by the driver
 * 
 * Implemented by: RideServiceImpl
 */
public interface RideService {

    /**
     * Create a new ride request
     * 
     * Creates a new ride with REQUESTED status and associates it with the
     * passenger.
     * 
     * @param request CreateRideRequest containing pickup and drop locations
     * @param userId  The ID of the passenger requesting the ride
     * @return The created Ride object with REQUESTED status
     */
    Ride createRide(CreateRideRequest request, String userId);

    /**
     * Get all rides for a specific passenger
     * 
     * Retrieves all ride requests (regardless of status) created by a specific
     * user.
     * 
     * @param userId The ID of the passenger
     * @return List of all rides created by this user
     */
    List<Ride> getUserRides(String userId);

    /**
     * Get all pending ride requests
     * 
     * Retrieves all rides with REQUESTED status that are waiting for driver
     * acceptance.
     * Used by drivers to view available ride requests.
     * 
     * @return List of all rides with REQUESTED status
     */
    List<Ride> getPendingRides();

    /**
     * Accept a pending ride request
     * 
     * Allows a driver to accept a pending ride request. Transitions the ride
     * from REQUESTED to ACCEPTED status and assigns the driver.
     * 
     * @param rideId   The ID of the ride to accept
     * @param driverId The ID of the driver accepting the ride
     * @return The updated Ride object with ACCEPTED status and driver assigned
     * @throws NotFoundException     if ride not found
     * @throws IllegalStateException if ride is not in REQUESTED status
     */
    Ride acceptRide(String rideId, String driverId);

    /**
     * Complete a ride
     * 
     * Marks an accepted ride as completed. Transitions the ride from ACCEPTED
     * to COMPLETED status, finalizing the transaction.
     * 
     * @param rideId The ID of the ride to complete
     * @return The updated Ride object with COMPLETED status
     * @throws NotFoundException     if ride not found
     * @throws IllegalStateException if ride is not in ACCEPTED status
     */
    Ride completeRide(String rideId);
}
