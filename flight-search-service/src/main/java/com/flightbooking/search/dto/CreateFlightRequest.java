package com.flightbooking.search.dto;

import com.flightbooking.common.enums.Carrier;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateFlightRequest {
    @NotNull
    private Carrier carrier;
    
    @NotBlank
    private String flightNumber;
    
    @NotBlank
    private String departureAirport;
    
    @NotBlank
    private String arrivalAirport;
    
    @NotNull
    private LocalDateTime departureTime;
    
    @NotNull
    private LocalDateTime arrivalTime;
    
    private String equipment;
}
