package org.example.rideshare.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Objects;

@Document(collection = "rides")
public class Ride {

    @Id
    private String id;

    private String userId; // passenger id
    private String driverId; // driver id (nullable)
    private String pickupLocation;
    private String dropLocation;
    private String status; // REQUESTED / ACCEPTED / COMPLETED
    private Date createdAt;

    public Ride() {
    }

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getDropLocation() {
        return dropLocation;
    }

    public void setDropLocation(String dropLocation) {
        this.dropLocation = dropLocation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Ride ride = (Ride) o;
        return Objects.equals(id, ride.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String userId;
        private String driverId;
        private String pickupLocation;
        private String dropLocation;
        private String status;
        private Date createdAt;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder driverId(String driverId) {
            this.driverId = driverId;
            return this;
        }

        public Builder pickupLocation(String pickupLocation) {
            this.pickupLocation = pickupLocation;
            return this;
        }

        public Builder dropLocation(String dropLocation) {
            this.dropLocation = dropLocation;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder createdAt(Date createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Ride build() {
            return new Ride(id, userId, driverId, pickupLocation, dropLocation, status, createdAt);
        }
    }
}
