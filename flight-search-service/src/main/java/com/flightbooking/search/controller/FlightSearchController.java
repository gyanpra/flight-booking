package com.flightbooking.search.controller;

import com.flightbooking.search.dto.FlightDetailsResponse;
import com.flightbooking.search.dto.FlightSearchRequest;
import com.flightbooking.search.dto.FlightSearchResponse;
import com.flightbooking.search.service.FlightSearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
public class FlightSearchController {
    
    private final FlightSearchService flightSearchService;
    
    @GetMapping("/search")
    public ResponseEntity<List<FlightSearchResponse>> searchFlights(@Valid @ModelAttribute FlightSearchRequest request) {
        List<FlightSearchResponse> results = flightSearchService.searchFlights(request);
        return ResponseEntity.ok(results);
    }
    
    @GetMapping("/{flightId}")
    public ResponseEntity<FlightDetailsResponse> getFlightDetails(@PathVariable UUID flightId) {
        FlightDetailsResponse response = flightSearchService.getFlightDetails(flightId);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/sync")
    public ResponseEntity<Void> syncToElasticsearch() {
        flightSearchService.syncFlightsToElasticsearch();
        return ResponseEntity.ok().build();
    }
}
