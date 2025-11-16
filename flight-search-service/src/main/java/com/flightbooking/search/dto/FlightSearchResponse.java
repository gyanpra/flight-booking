package com.flightbooking.search.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.flightbooking.common.enums.CabinClass;
import com.flightbooking.common.enums.Carrier;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightSearchResponse {
    public UUID flightId;
    public Carrier carrier;
    public String flightNumber;
    public String origin;
    public String destination;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    public LocalDateTime departureTime;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    public LocalDateTime arrivalTime;
    public Integer durationMinutes;
    public CabinClass cabinClass;
    public BigDecimal price;
    public String currency;
    public Integer availableSeats;
}
