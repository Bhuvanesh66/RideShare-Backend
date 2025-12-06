package org.example.rideshare.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Objects;

/**
 * Ride - Entity model representing a ride request in the RideShare platform
 * 
 * This model represents a ride transaction between a passenger (user) and a
 * driver.
 * A ride lifecycle includes:
 * 1. REQUESTED: Initially created by a passenger, awaiting driver acceptance
 * 2. ACCEPTED: A driver has accepted the ride request
 * 3. COMPLETED: The ride has been completed and finished
 * 
 * Key relationships:
 * - userId: References the passenger who requested the ride
 * - driverId: References the driver who accepted the ride (null if not yet
 * accepted)
 * 
 * Stored in MongoDB "rides" collection for scalability and flexibility.
 * 
 * @author RideShare Development Team
 * @version 1.0
 */
@Document(collection = "rides")
public class Ride {

    // MongoDB document ID - automatically generated
    @Id
    private String id;

    // Reference to the passenger (User) who requested this ride
    private String userId;

    // Reference to the driver (User) who accepted this ride (null if pending)
    private String driverId;

    // Starting location for the ride
    private String pickupLocation;

    // Destination location for the ride
    private String dropLocation;

    // Current status: REQUESTED, ACCEPTED, or COMPLETED
    private String status;

    // Timestamp when the ride was created
    private Date createdAt;

    // Default constructor for MongoDB and frameworks
    public Ride() {
    }

    // Constructor to initialize all fields
    public Ride(String id, String userId, String driverId, String pickupLocation, String dropLocation, String status,
            Date createdAt) {
        this.id = id;
        this.userId = userId;
        this.driverId = driverId;
        this.pickupLocation = pickupLocation;
        this.dropLocation = dropLocation;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getter for ride ID
    public String getId() {
        return id;
    }

    // Setter for ride ID
    public void setId(String id) {
        this.id = id;
    }

    // Getter for passenger user ID
    public String getUserId() {
        return userId;
    }

    // Setter for passenger user ID
    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Getter for driver user ID
    public String getDriverId() {
        return driverId;
    }

    // Setter for driver user ID
    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    // Getter for pickup location
    public String getPickupLocation() {
        return pickupLocation;
    }

    // Setter for pickup location
    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    // Getter for drop-off location
    public String getDropLocation() {
        return dropLocation;
    }

    // Setter for drop-off location
    public void setDropLocation(String dropLocation) {
        this.dropLocation = dropLocation;
    }

    // Getter for ride status
    public String getStatus() {
        return status;
    }

    // Setter for ride status
    public void setStatus(String status) {
        this.status = status;
    }

    // Getter for creation timestamp
    public Date getCreatedAt() {
        return createdAt;
    }

    // Setter for creation timestamp
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    // Equality comparison based on ride ID
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Ride ride = (Ride) o;
        return Objects.equals(id, ride.id);
    }

    // Hash code based on ride ID
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // Static factory method to get a Ride builder
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder - Fluent API for constructing Ride instances
     * 
     * Provides a clean and readable way to build Ride objects with optional fields.
     * Example: Ride ride =
     * Ride.builder().userId("user1").status("REQUESTED").build();
     */
    public static class Builder {
        private String id;
        private String userId;
        private String driverId;
        private String pickupLocation;
        private String dropLocation;
        private String status;
        private Date createdAt;

        // Set the ride ID
        public Builder id(String id) {
            this.id = id;
            return this;
        }

        // Set the passenger user ID
        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        // Set the driver user ID
        public Builder driverId(String driverId) {
            this.driverId = driverId;
            return this;
        }

        // Set the pickup location
        public Builder pickupLocation(String pickupLocation) {
            this.pickupLocation = pickupLocation;
            return this;
        }

        // Set the drop-off location
        public Builder dropLocation(String dropLocation) {
            this.dropLocation = dropLocation;
            return this;
        }

        // Set the ride status
        public Builder status(String status) {
            this.status = status;
            return this;
        }

        // Set the creation timestamp
        public Builder createdAt(Date createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        // Build and return the Ride object
        public Ride build() {
            return new Ride(id, userId, driverId, pickupLocation, dropLocation, status, createdAt);
        }
    }
}
