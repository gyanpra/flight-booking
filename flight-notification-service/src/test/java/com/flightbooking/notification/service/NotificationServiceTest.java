package com.flightbooking.notification.service;

import com.flightbooking.common.enums.BookingStatus;
import com.flightbooking.common.event.BookingEvent;
import com.flightbooking.notification.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {
    
    @InjectMocks
    private NotificationServiceImpl notificationService;
    
    private BookingEvent testEvent;
    
    @BeforeEach
    void setUp() {
        testEvent = new BookingEvent();
        testEvent.setBookingId(UUID.randomUUID());
        testEvent.setPnr("ABC123");
        testEvent.setUserId(UUID.randomUUID());
        testEvent.setStatus(BookingStatus.CONFIRMED);
        testEvent.setAmount(BigDecimal.valueOf(10000));
        testEvent.setCurrency("INR");
        testEvent.setUserEmail("john@example.com");
        testEvent.setUserPhone("+919876543210");
        testEvent.setTimestamp(LocalDateTime.now());
    }
    
    @Test
    void testSendBookingConfirmation() {
        assertDoesNotThrow(() -> notificationService.sendBookingConfirmation(testEvent));
    }
    
    @Test
    void testSendBookingCancellation() {
        testEvent.setStatus(BookingStatus.CANCELLED);
        assertDoesNotThrow(() -> notificationService.sendBookingCancellation(testEvent));
    }
}
