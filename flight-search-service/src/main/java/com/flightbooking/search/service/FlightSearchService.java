package com.flightbooking.search.service;

import com.flightbooking.search.dto.*;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for flight search operations.
 * Handles searching flights from Elasticsearch and PostgreSQL.
 */
public interface FlightSearchService {
    
    /**
     * Search flights based on criteria
     */
    List<FlightSearchResponse> searchFlights(FlightSearchRequest request);
    
    /**
     * Get flight details by ID
     */
    FlightDetailsResponse getFlightDetails(UUID flightId);
    
    /**
     * Sync flight data from PostgreSQL to Elasticsearch
     */
    void syncFlightsToElasticsearch();
    
    /**
     * Create new flight (Carrier API)
     */
    FlightCreatedResponse createFlight(CreateFlightRequest request);
    
    /**
     * Add seat inventory to flight (Carrier API)
     */
    void addInventory(AddInventoryRequest request);
}
