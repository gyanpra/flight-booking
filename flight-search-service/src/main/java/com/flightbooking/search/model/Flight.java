package com.flightbooking.search.model;

import com.flightbooking.common.enums.Carrier;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "flights", indexes = {
    @Index(name = "idx_route_time", columnList = "departure_airport,arrival_airport,departure_time"),
    @Index(name = "idx_carrier_flight", columnList = "carrier,flight_number,departure_time")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID flightId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Carrier carrier;
    
    @Column(nullable = false)
    private String flightNumber;
    
    @Column(nullable = false)
    private String departureAirport;
    
    @Column(nullable = false)
    private String arrivalAirport;
    
    @Column(nullable = false)
    private LocalDateTime departureTime;
    
    @Column(nullable = false)
    private LocalDateTime arrivalTime;
    
    private String equipment;
    
    @Column(nullable = false)
    private Boolean isActive = true;
}
