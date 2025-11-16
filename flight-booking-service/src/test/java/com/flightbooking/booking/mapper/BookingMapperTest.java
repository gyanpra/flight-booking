package com.flightbooking.booking.mapper;

import com.flightbooking.booking.dto.BookingResponse;
import com.flightbooking.booking.model.Booking;
import com.flightbooking.common.enums.BookingStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {
    
    private BookingMapper bookingMapper;
    private Booking testBooking;
    
    @BeforeEach
    void setUp() {
        bookingMapper = new BookingMapper();
        
        testBooking = new Booking();
        testBooking.setBookingId(UUID.randomUUID());
        testBooking.setPnr("ABC123");
        testBooking.setUserId(UUID.randomUUID());
        testBooking.setItineraryId(UUID.randomUUID());
        testBooking.setBookingStatus(BookingStatus.CONFIRMED);
        testBooking.setAmount(BigDecimal.valueOf(10000));
        testBooking.setCurrency("INR");
        testBooking.setCreatedAt(LocalDateTime.now());
        
        Booking.PassengerInfo passenger = new Booking.PassengerInfo(
            "John", "Doe", 30, "M", "PASSPORT", "AB123456"
        );
        testBooking.setPassengers(Arrays.asList(passenger));
        
        Booking.SeatInfo seat = new Booking.SeatInfo(UUID.randomUUID(), "12A", "ECONOMY");
        testBooking.setSeats(Arrays.asList(seat));
    }
    
    @Test
    void testToResponse() {
        BookingResponse response = bookingMapper.toResponse(testBooking);
        
        assertNotNull(response);
        assertEquals(testBooking.getBookingId(), response.getBookingId());
        assertEquals(testBooking.getPnr(), response.getPnr());
        assertEquals(testBooking.getUserId(), response.getUserId());
        assertEquals(testBooking.getBookingStatus(), response.getBookingStatus());
        assertEquals(testBooking.getAmount(), response.getAmount());
        assertEquals(1, response.getPassengers().size());
        assertEquals(1, response.getSeats().size());
    }
}
