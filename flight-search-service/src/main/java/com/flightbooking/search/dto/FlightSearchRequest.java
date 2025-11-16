package com.flightbooking.search.dto;

import com.flightbooking.common.enums.CabinClass;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightSearchRequest {
    @NotBlank
    private String origin;
    
    @NotBlank
    private String destination;
    
    @NotNull
    private LocalDate departureDate;
    
    @NotNull
    @Min(1)
    private Integer passengers;
    
    private CabinClass cabinClass;
    
    private Integer maxStops;
}
