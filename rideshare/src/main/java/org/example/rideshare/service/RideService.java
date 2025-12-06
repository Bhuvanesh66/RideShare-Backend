package org.example.rideshare.service;

import org.example.rideshare.dto.CreateRideRequest;
import org.example.rideshare.model.Ride;

import java.util.List;

public interface RideService {

    Ride createRide(CreateRideRequest request, String userId);

    List<Ride> getUserRides(String userId);

    List<Ride> getPendingRides();

    Ride acceptRide(String rideId, String driverId);

    Ride completeRide(String rideId);
}
