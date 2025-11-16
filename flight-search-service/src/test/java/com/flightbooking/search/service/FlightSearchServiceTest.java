package com.flightbooking.search.service;

import com.flightbooking.common.enums.CabinClass;
import com.flightbooking.common.enums.Carrier;
import com.flightbooking.search.dto.FlightDetailsResponse;
import com.flightbooking.search.dto.FlightSearchRequest;
import com.flightbooking.search.dto.FlightSearchResponse;
import com.flightbooking.search.mapper.FlightMapper;
import com.flightbooking.search.model.Flight;
import com.flightbooking.search.model.SeatInventory;
import com.flightbooking.search.repository.FlightDocumentRepository;
import com.flightbooking.search.repository.FlightRepository;
import com.flightbooking.search.repository.SeatInventoryRepository;
import com.flightbooking.search.service.impl.FlightSearchServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightSearchServiceTest {
    
    @Mock
    private FlightRepository flightRepository;
    
    @Mock
    private SeatInventoryRepository seatInventoryRepository;
    
    @Mock
    private FlightDocumentRepository flightDocumentRepository;
    
    @Mock
    private ElasticsearchOperations elasticsearchOperations;
    
    @Mock
    private FlightMapper flightMapper;
    
    @InjectMocks
    private FlightSearchServiceImpl flightSearchService;
    
    private Flight testFlight;
    private SeatInventory testInventory;
    
    @BeforeEach
    void setUp() {
        testFlight = new Flight();
        testFlight.setFlightId(UUID.randomUUID());
        testFlight.setCarrier(Carrier.INDIGO);
        testFlight.setFlightNumber("6E-123");
        testFlight.setDepartureAirport("BLR");
        testFlight.setArrivalAirport("DEL");
        testFlight.setDepartureTime(LocalDateTime.now().plusDays(1));
        testFlight.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(2));
        testFlight.setIsActive(true);
        
        testInventory = new SeatInventory();
        testInventory.setFlightId(testFlight.getFlightId());
        testInventory.setFareClass("Y");
        testInventory.setCabinClass(CabinClass.ECONOMY);
        testInventory.setTotalSeats(180);
        testInventory.setAvailableSeats(50);
        testInventory.setPrice(BigDecimal.valueOf(5000));
    }
    
    @Test
    void testSearchFlights_Success() {
        FlightSearchRequest request = new FlightSearchRequest();
        request.setOrigin("BLR");
        request.setDestination("DEL");
        request.setDepartureDate(LocalDate.now().plusDays(1));
        request.setPassengers(2);
        request.setCabinClass(CabinClass.ECONOMY);
        
        when(flightRepository.findFlightsByRoute(any(), any(), any(), any()))
            .thenReturn(Arrays.asList(testFlight));
        when(seatInventoryRepository.findByFlightId(any()))
            .thenReturn(Arrays.asList(testInventory));
        when(flightMapper.toDocument(any(), any())).thenCallRealMethod();
        when(flightMapper.toSearchResponse(any())).thenReturn(new FlightSearchResponse());
        
        List<FlightSearchResponse> results = flightSearchService.searchFlights(request);
        
        assertNotNull(results);
        verify(flightRepository).findFlightsByRoute(any(), any(), any(), any());
    }
    
    @Test
    void testGetFlightDetails_Success() {
        UUID flightId = testFlight.getFlightId();
        
        when(flightRepository.findById(flightId)).thenReturn(Optional.of(testFlight));
        when(seatInventoryRepository.findByFlightId(flightId))
            .thenReturn(Arrays.asList(testInventory));
        when(flightMapper.toDetailsResponse(any(), any()))
            .thenReturn(new FlightDetailsResponse());
        
        FlightDetailsResponse response = flightSearchService.getFlightDetails(flightId);
        
        assertNotNull(response);
        verify(flightRepository).findById(flightId);
        verify(seatInventoryRepository).findByFlightId(flightId);
    }
    
    @Test
    void testSyncFlightsToElasticsearch() {
        when(flightRepository.findAll()).thenReturn(Arrays.asList(testFlight));
        when(seatInventoryRepository.findByFlightId(any()))
            .thenReturn(Arrays.asList(testInventory));
        when(flightMapper.toDocument(any(), any())).thenCallRealMethod();
        
        flightSearchService.syncFlightsToElasticsearch();
        
        verify(flightRepository).findAll();
        verify(flightDocumentRepository, atLeastOnce()).save(any());
    }
}
