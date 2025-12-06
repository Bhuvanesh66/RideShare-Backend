package org.example.rideshare.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {

    @GetMapping("/")
    public String welcome() {
        return "{\"message\": \"Welcome to RideShare API\", \"version\": \"1.0\", \"endpoints\": [" +
                "\"/api/auth/register\", " +
                "\"/api/auth/login\", " +
                "\"/api/v1/rides\", " +
                "\"/api/v1/user/rides\", " +
                "\"/api/v1/driver/rides/requests\", " +
                "\"/api/v1/driver/rides/{id}/accept\", " +
                "\"/api/v1/rides/{id}/complete\"]}";
    }

    @GetMapping("/health")
    public String health() {
        return "{\"status\": \"UP\", \"service\": \"RideShare API\"}";
    }
}
