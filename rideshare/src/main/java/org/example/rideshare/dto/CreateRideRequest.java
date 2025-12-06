package org.example.rideshare.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * CreateRideRequest - Data Transfer Object for creating a new ride
 * 
 * This DTO encapsulates the information required to request a new ride.
 * Both pickup and drop-off locations are required.
 * 
 * Validation Rules:
 * - pickupLocation: Must not be blank
 * - dropLocation: Must not be blank
 * 
 * Used by: RideController.createRide()
 * 
 * Security: Requires authentication (Bearer token with ROLE_USER)
 */
public class CreateRideRequest {

    // Starting location for the ride (must not be blank)
    @NotBlank(message = "Pickup is required")
    private String pickupLocation;

    // Destination location for the ride (must not be blank)
    @NotBlank(message = "Drop is required")
    private String dropLocation;

    // Default constructor for deserialization
    public CreateRideRequest() {
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
}
