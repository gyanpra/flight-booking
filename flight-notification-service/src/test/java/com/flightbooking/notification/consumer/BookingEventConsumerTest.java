package com.flightbooking.notification.consumer;

import com.flightbooking.common.enums.BookingStatus;
import com.flightbooking.common.event.BookingEvent;
import com.flightbooking.notification.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingEventConsumerTest {
    
    @Mock
    private NotificationService notificationService;
    
    @InjectMocks
    private BookingEventConsumer consumer;
    
    @Test
    void testConsumeConfirmedBookingEvent() {
        BookingEvent event = new BookingEvent();
        event.setBookingId(UUID.randomUUID());
        event.setPnr("ABC123");
        event.setStatus(BookingStatus.CONFIRMED);
        event.setAmount(BigDecimal.valueOf(10000));
        event.setCurrency("INR");
        event.setUserEmail("john@example.com");
        event.setUserPhone("+919876543210");
        event.setTimestamp(LocalDateTime.now());
        
        consumer.consumeBookingEvent(event);
        
        verify(notificationService).sendBookingConfirmation(event);
    }
    
    @Test
    void testConsumeCancelledBookingEvent() {
        BookingEvent event = new BookingEvent();
        event.setBookingId(UUID.randomUUID());
        event.setPnr("ABC123");
        event.setStatus(BookingStatus.CANCELLED);
        event.setTimestamp(LocalDateTime.now());
        
        consumer.consumeBookingEvent(event);
        
        verify(notificationService).sendBookingCancellation(event);
    }
}
