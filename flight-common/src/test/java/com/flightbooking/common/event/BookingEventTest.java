package com.flightbooking.common.event;

import com.flightbooking.common.enums.BookingStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BookingEventTest {
    
    @Test
    void testBookingEvent() {
        BookingEvent event = new BookingEvent();
        event.setBookingId(UUID.randomUUID());
        event.setPnr("ABC123");
        event.setUserId(UUID.randomUUID());
        event.setStatus(BookingStatus.CONFIRMED);
        event.setAmount(BigDecimal.valueOf(10000));
        event.setCurrency("INR");
        event.setUserEmail("john@example.com");
        event.setUserPhone("+919876543210");
        event.setTimestamp(LocalDateTime.now());
        
        assertEquals("ABC123", event.getPnr());
        assertEquals(BookingStatus.CONFIRMED, event.getStatus());
        assertEquals("INR", event.getCurrency());
    }
}
