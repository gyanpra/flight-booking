package com.flightbooking.search.model;

import com.flightbooking.common.enums.CabinClass;
import com.flightbooking.common.enums.Carrier;
import com.flightbooking.common.enums.HoldStatus;
import com.flightbooking.common.enums.SeatStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest {
    
    @Test
    void testFlight() {
        Flight flight = new Flight();
        flight.setFlightId(UUID.randomUUID());
        flight.setCarrier(Carrier.INDIGO);
        flight.setFlightNumber("6E-123");
        flight.setDepartureAirport("BLR");
        flight.setArrivalAirport("DEL");
        flight.setDepartureTime(LocalDateTime.now());
        flight.setArrivalTime(LocalDateTime.now().plusHours(2));
        flight.setEquipment("A320");
        flight.setIsActive(true);
        
        assertNotNull(flight.getFlightId());
        assertEquals(Carrier.INDIGO, flight.getCarrier());
        assertEquals("6E-123", flight.getFlightNumber());
        assertTrue(flight.getIsActive());
    }
    
    @Test
    void testSeatInventory() {
        SeatInventory inv = new SeatInventory();
        inv.setId(1L);
        inv.setFlightId(UUID.randomUUID());
        inv.setFareClass("Y");
        inv.setCabinClass(CabinClass.ECONOMY);
        inv.setTotalSeats(180);
        inv.setAvailableSeats(150);
        inv.setPrice(BigDecimal.valueOf(5000));
        inv.setVersion(0L);
        
        assertEquals(1L, inv.getId());
        assertEquals("Y", inv.getFareClass());
        assertEquals(180, inv.getTotalSeats());
    }
    
    @Test
    void testSeatMap() {
        SeatMap seat = new SeatMap();
        seat.setId(1L);
        seat.setFlightId(UUID.randomUUID());
        seat.setSeatNo("12A");
        seat.setCabinClass(CabinClass.ECONOMY);
        seat.setFareClass("Y");
        seat.setStatus(SeatStatus.AVAILABLE);
        seat.setCurrentHoldId(UUID.randomUUID());
        
        assertEquals("12A", seat.getSeatNo());
        assertEquals(SeatStatus.AVAILABLE, seat.getStatus());
    }
    
    @Test
    void testInventoryHold() {
        InventoryHold hold = new InventoryHold();
        hold.setHoldId(UUID.randomUUID());
        hold.setFlightId(UUID.randomUUID());
        hold.setUserId(UUID.randomUUID());
        hold.setCustomerSessionId("session123");
        hold.setSeatCount(2);
        hold.setSeats(Arrays.asList("12A", "12B"));
        hold.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        hold.setStatus(HoldStatus.ACTIVE);
        hold.setCreatedAt(LocalDateTime.now());
        
        assertEquals(2, hold.getSeatCount());
        assertEquals(HoldStatus.ACTIVE, hold.getStatus());
    }
    
    @Test
    void testFlightDocument() {
        FlightDocument doc = new FlightDocument();
        doc.setItineraryId("itin123");
        doc.setFlightId(UUID.randomUUID());
        doc.setCarrier(Carrier.INDIGO);
        doc.setFlightNumber("6E-123");
        doc.setOrigin("BLR");
        doc.setDestination("DEL");
        doc.setDepartureTime(LocalDateTime.now());
        doc.setArrivalTime(LocalDateTime.now().plusHours(2));
        doc.setDurationMinutes(120);
        doc.setCabinClass(CabinClass.ECONOMY);
        doc.setPrice(BigDecimal.valueOf(5000));
        doc.setCurrency("INR");
        doc.setAvailableSeats(150);
        doc.setStops(0);
        
        assertEquals("itin123", doc.getItineraryId());
        assertEquals(120, doc.getDurationMinutes());
    }
}
