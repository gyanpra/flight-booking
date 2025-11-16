package com.flightbooking.search.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightCreatedResponse {
    private UUID flightId;
    private String flightNumber;
    private String message;
}
