package com.flightbooking.search.controller;

import com.flightbooking.search.dto.AddInventoryRequest;
import com.flightbooking.search.dto.CreateFlightRequest;
import com.flightbooking.search.dto.FlightCreatedResponse;
import com.flightbooking.search.service.FlightSearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrier")
@RequiredArgsConstructor
public class CarrierController {
    
    private final FlightSearchService flightSearchService;
    
    @PostMapping("/flights")
    public ResponseEntity<FlightCreatedResponse> createFlight(@Valid @RequestBody CreateFlightRequest request) {
        FlightCreatedResponse response = flightSearchService.createFlight(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/inventory")
    public ResponseEntity<Void> addInventory(@Valid @RequestBody AddInventoryRequest request) {
        flightSearchService.addInventory(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
