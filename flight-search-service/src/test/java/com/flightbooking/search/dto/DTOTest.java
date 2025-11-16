package com.flightbooking.search.dto;

import com.flightbooking.common.enums.CabinClass;
import com.flightbooking.common.enums.Carrier;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DTOTest {
    
    @Test
    void testFlightSearchRequest() {
        FlightSearchRequest req = new FlightSearchRequest();
        req.setOrigin("BLR");
        req.setDestination("DEL");
        req.setDepartureDate(LocalDate.now());
        req.setPassengers(2);
        req.setCabinClass(CabinClass.ECONOMY);
        req.setMaxStops(1);
        
        assertEquals("BLR", req.getOrigin());
        assertEquals(2, req.getPassengers());
    }
    
    @Test
    void testFlightSearchResponse() {
        FlightSearchResponse res = new FlightSearchResponse();
        res.setFlightId(UUID.randomUUID());
        res.setCarrier(Carrier.INDIGO);
        res.setFlightNumber("6E-123");
        res.setOrigin("BLR");
        res.setDestination("DEL");
        res.setDepartureTime(LocalDateTime.now());
        res.setArrivalTime(LocalDateTime.now().plusHours(2));
        res.setDurationMinutes(120);
        res.setCabinClass(CabinClass.ECONOMY);
        res.setPrice(BigDecimal.valueOf(5000));
        res.setCurrency("INR");
        res.setAvailableSeats(150);
        
        assertEquals("6E-123", res.getFlightNumber());
        assertEquals(150, res.getAvailableSeats());
    }
    
    @Test
    void testFlightDetailsResponse() {
        FlightDetailsResponse res = new FlightDetailsResponse();
        res.setFlightId(UUID.randomUUID());
        res.setCarrier(Carrier.INDIGO);
        res.setFlightNumber("6E-123");
        res.setDepartureAirport("BLR");
        res.setArrivalAirport("DEL");
        res.setDepartureTime(LocalDateTime.now());
        res.setArrivalTime(LocalDateTime.now().plusHours(2));
        res.setEquipment("A320");
        res.setIsActive(true);
        
        FlightDetailsResponse.SeatInventoryInfo inv = new FlightDetailsResponse.SeatInventoryInfo();
        inv.setFareClass("Y");
        inv.setCabinClass("ECONOMY");
        inv.setAvailableSeats(150);
        inv.setPrice(BigDecimal.valueOf(5000));
        res.setSeatInventory(Arrays.asList(inv));
        
        assertEquals("A320", res.getEquipment());
        assertEquals(1, res.getSeatInventory().size());
    }
}
