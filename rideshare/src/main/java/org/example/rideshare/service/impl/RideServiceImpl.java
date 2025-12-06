package org.example.rideshare.service.impl;

import org.example.rideshare.dto.CreateRideRequest;
import org.example.rideshare.exception.NotFoundException;
import org.example.rideshare.model.Ride;
import org.example.rideshare.repository.RideRepository;
import org.example.rideshare.service.RideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class RideServiceImpl implements RideService {

    @Autowired
    private RideRepository rideRepository;

    @Override
    public Ride createRide(CreateRideRequest request, String userId) {
        Ride ride = Ride.builder()
                .userId(userId)
                .pickupLocation(request.getPickupLocation())
                .dropLocation(request.getDropLocation())
                .status("REQUESTED")
                .createdAt(new Date())
                .build();

        return rideRepository.save(ride);
    }

    @Override
    public List<Ride> getUserRides(String userId) {
        return rideRepository.findByUserId(userId);
    }

    @Override
    public List<Ride> getPendingRides() {
        return rideRepository.findByStatus("REQUESTED");
    }

    @Override
    public Ride acceptRide(String rideId, String driverId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new NotFoundException("Ride not found"));

        if (!"REQUESTED".equals(ride.getStatus())) {
            throw new IllegalStateException("Ride is not in REQUESTED status");
        }

        ride.setStatus("ACCEPTED");
        ride.setDriverId(driverId);
        return rideRepository.save(ride);
    }

    @Override
    public Ride completeRide(String rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new NotFoundException("Ride not found"));

        if (!"ACCEPTED".equals(ride.getStatus())) {
            throw new IllegalStateException("Ride is not in ACCEPTED status");
        }

        ride.setStatus("COMPLETED");
        return rideRepository.save(ride);
    }
}
