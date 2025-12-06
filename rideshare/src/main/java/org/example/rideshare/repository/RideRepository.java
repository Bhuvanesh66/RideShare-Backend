package org.example.rideshare.repository;

import org.example.rideshare.model.Ride;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * RideRepository - Data access layer for Ride entities
 * 
 * This Spring Data MongoDB repository provides database operations for Ride
 * entities.
 * It extends MongoRepository which provides CRUD operations (Create, Read,
 * Update, Delete)
 * automatically. Custom query methods enable filtering by status and passenger.
 * 
 * Inherited Methods (from MongoRepository):
 * - save(Ride) - Create or update a ride
 * - findById(String) - Get ride by ID
 * - findAll() - Get all rides
 * - delete(Ride) - Delete a ride
 * - deleteById(String) - Delete ride by ID
 * 
 * Custom Methods:
 * - findByStatus(String) - Find all rides with a specific status
 * - findByUserId(String) - Find all rides for a specific passenger
 * 
 * Used by: RideService for ride lookups and queries
 */
public interface RideRepository extends MongoRepository<Ride, String> {

    /**
     * Find all rides with a specific status
     * 
     * Status values: REQUESTED, ACCEPTED, COMPLETED
     * Used to retrieve rides in a particular state.
     * 
     * @param status The ride status to filter by
     * @return List of rides with the specified status
     */
    List<Ride> findByStatus(String status);

    /**
     * Find all rides for a specific passenger (user)
     * 
     * Returns all ride requests created by a particular user,
     * regardless of their status.
     * 
     * @param userId The passenger user ID
     * @return List of all rides created by this user
     */
    List<Ride> findByUserId(String userId);
}
