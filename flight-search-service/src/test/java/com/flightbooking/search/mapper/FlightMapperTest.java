package com.flightbooking.search.mapper;

import com.flightbooking.common.enums.CabinClass;
import com.flightbooking.common.enums.Carrier;
import com.flightbooking.search.dto.FlightDetailsResponse;
import com.flightbooking.search.model.Flight;
import com.flightbooking.search.model.FlightDocument;
import com.flightbooking.search.model.SeatInventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FlightMapperTest {
    
    private FlightMapper flightMapper;
    private Flight testFlight;
    private SeatInventory testInventory;
    
    @BeforeEach
    void setUp() {
        flightMapper = new FlightMapper();
        
        testFlight = new Flight();
        testFlight.setFlightId(UUID.randomUUID());
        testFlight.setCarrier(Carrier.INDIGO);
        testFlight.setFlightNumber("6E-123");
        testFlight.setDepartureAirport("BLR");
        testFlight.setArrivalAirport("DEL");
        testFlight.setDepartureTime(LocalDateTime.now());
        testFlight.setArrivalTime(LocalDateTime.now().plusHours(2));
        testFlight.setEquipment("A320");
        testFlight.setIsActive(true);
        
        testInventory = new SeatInventory();
        testInventory.setFareClass("Y");
        testInventory.setCabinClass(CabinClass.ECONOMY);
        testInventory.setAvailableSeats(50);
        testInventory.setPrice(BigDecimal.valueOf(5000));
    }
    
    @Test
    void testToDocument() {
        FlightDocument doc = flightMapper.toDocument(testFlight, testInventory);
        
        assertNotNull(doc);
        assertEquals(testFlight.getFlightId(), doc.getFlightId());
        assertEquals(testFlight.getCarrier(), doc.getCarrier());
        assertEquals(testFlight.getFlightNumber(), doc.getFlightNumber());
        assertEquals(testFlight.getDepartureAirport(), doc.getOrigin());
        assertEquals(testFlight.getArrivalAirport(), doc.getDestination());
        assertEquals(testInventory.getCabinClass(), doc.getCabinClass());
        assertEquals(testInventory.getPrice(), doc.getPrice());
        assertEquals(testInventory.getAvailableSeats(), doc.getAvailableSeats());
    }
    
    @Test
    void testToDetailsResponse() {
        FlightDetailsResponse response = flightMapper.toDetailsResponse(
            testFlight, Arrays.asList(testInventory)
        );
        
        assertNotNull(response);
        assertEquals(testFlight.getFlightId(), response.getFlightId());
        assertEquals(testFlight.getCarrier(), response.getCarrier());
        assertEquals(testFlight.getFlightNumber(), response.getFlightNumber());
        assertEquals(1, response.getSeatInventory().size());
    }
}
