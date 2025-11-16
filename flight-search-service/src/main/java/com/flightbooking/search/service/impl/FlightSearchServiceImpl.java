package com.flightbooking.search.service.impl;

import com.flightbooking.common.exception.ResourceNotFoundException;
import com.flightbooking.search.dto.FlightDetailsResponse;
import com.flightbooking.search.dto.FlightSearchRequest;
import com.flightbooking.search.dto.FlightSearchResponse;
import com.flightbooking.search.mapper.FlightMapper;
import com.flightbooking.search.model.Flight;
import com.flightbooking.search.model.FlightDocument;
import com.flightbooking.search.model.SeatInventory;
import com.flightbooking.search.repository.FlightDocumentRepository;
import com.flightbooking.search.repository.FlightRepository;
import com.flightbooking.search.repository.SeatInventoryRepository;
import com.flightbooking.search.service.FlightSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlightSearchServiceImpl implements FlightSearchService {
    
    private final FlightRepository flightRepository;
    private final SeatInventoryRepository seatInventoryRepository;
    private final FlightDocumentRepository flightDocumentRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final FlightMapper flightMapper;

    @Override
    @Cacheable(value = "flightSearch", key = "#request.origin + '-' + #request.destination + '-' + #request.departureDate + '-' + #request.passengers")
    public List<FlightSearchResponse> searchFlights(FlightSearchRequest request) {
        log.info("Searching flights: {} to {} on {}", request.getOrigin(), 
                 request.getDestination(), request.getDepartureDate());
        
        LocalDateTime startTime = request.getDepartureDate().atStartOfDay();
        LocalDateTime endTime = startTime.plusDays(1);
        
        // Search from database as fallback
        List<Flight> flights = flightRepository.findFlightsByRoute(
            request.getOrigin(), 
            request.getDestination(), 
            startTime, 
            endTime
        );
        
        return flights.stream()
            .flatMap(flight -> {
                List<SeatInventory> inventory = seatInventoryRepository.findByFlightId(flight.getFlightId());
                return inventory.stream()
                    .filter(inv -> inv.getAvailableSeats() >= request.getPassengers())
                    .filter(inv -> request.getCabinClass() == null || 
                                   inv.getCabinClass() == request.getCabinClass())
                    .map(inv -> flightMapper.toDocument(flight, inv))
                    .map(flightMapper::toSearchResponse);
            })
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public FlightDetailsResponse getFlightDetails(UUID flightId) {
        Flight flight = flightRepository.findById(flightId)
            .orElseThrow(() -> new ResourceNotFoundException("Flight not found: " + flightId));
        
        List<SeatInventory> inventory = seatInventoryRepository.findByFlightId(flightId);
        
        return flightMapper.toDetailsResponse(flight, inventory);
    }
    
    @Override
    @Transactional
    public void syncFlightsToElasticsearch() {
        log.info("Starting flight sync to Elasticsearch");
        
        List<Flight> flights = flightRepository.findAll();
        
        for (Flight flight : flights) {
            List<SeatInventory> inventory = seatInventoryRepository.findByFlightId(flight.getFlightId());
            for (SeatInventory inv : inventory) {
                FlightDocument doc = flightMapper.toDocument(flight, inv);
                flightDocumentRepository.save(doc);
            }
        }
        
        log.info("Completed syncing {} flights to Elasticsearch", flights.size());
    }
}
