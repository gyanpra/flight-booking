package com.flightbooking.search.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.flightbooking.common.enums.Carrier;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlightDetailsResponse {
    private UUID flightId;
    private Carrier carrier;
    private String flightNumber;
    private String departureAirport;
    private String arrivalAirport;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime departureTime;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime arrivalTime;
    private String equipment;
    private Boolean isActive;
    private List<SeatInventoryInfo> seatInventory;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SeatInventoryInfo {
        private String fareClass;
        private String cabinClass;
        private Integer availableSeats;
        private java.math.BigDecimal price;
    }
}
