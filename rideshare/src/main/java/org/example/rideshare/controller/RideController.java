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

@RestController
@RequestMapping("/api/v1")
public class RideController {

    @Autowired
    private RideService rideService;

    @Autowired
    private UserRepository userRepository;

    // USER: Create Ride
    @PostMapping("/rides")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<Ride> createRide(@Valid @RequestBody CreateRideRequest request) {
        String username = SecurityUtil.getCurrentUsername();
        User user = userRepository.findByUsername(username).orElseThrow();
        Ride ride = rideService.createRide(request, user.getId());
        return ResponseEntity.ok(ride);
    }

    // USER: Get own rides
    @GetMapping("/user/rides")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<List<Ride>> getMyRides() {
        String username = SecurityUtil.getCurrentUsername();
        User user = userRepository.findByUsername(username).orElseThrow();
        return ResponseEntity.ok(rideService.getUserRides(user.getId()));
    }

    // DRIVER: View pending requested rides
    @GetMapping("/driver/rides/requests")
    @PreAuthorize("hasAuthority('ROLE_DRIVER')")
    public ResponseEntity<List<Ride>> getPendingRides() {
        return ResponseEntity.ok(rideService.getPendingRides());
    }

    // DRIVER: Accept ride
    @PostMapping("/driver/rides/{rideId}/accept")
    @PreAuthorize("hasAuthority('ROLE_DRIVER')")
    public ResponseEntity<Ride> acceptRide(@PathVariable String rideId) {
        String username = SecurityUtil.getCurrentUsername();
        User driver = userRepository.findByUsername(username).orElseThrow();
        return ResponseEntity.ok(rideService.acceptRide(rideId, driver.getId()));
    }

    // USER/DRIVER: Complete ride
    @PostMapping("/rides/{rideId}/complete")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_DRIVER')")
    public ResponseEntity<Ride> completeRide(@PathVariable String rideId) {
        return ResponseEntity.ok(rideService.completeRide(rideId));
    }
}
