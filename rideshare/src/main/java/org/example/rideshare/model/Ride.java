package org.example.rideshare.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "rides")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ride {

    @Id
    private String id;

    private String userId; // passenger id
    private String driverId; // driver id (nullable)
    private String pickupLocation;
    private String dropLocation;
    private String status; // REQUESTED / ACCEPTED / COMPLETED
    private Date createdAt;
}
