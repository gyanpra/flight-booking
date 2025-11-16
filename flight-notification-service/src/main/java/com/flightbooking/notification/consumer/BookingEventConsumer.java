package com.flightbooking.notification.consumer;

import com.flightbooking.common.enums.BookingStatus;
import com.flightbooking.common.event.BookingEvent;
import com.flightbooking.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingEventConsumer {
    
    private final NotificationService notificationService;
    
    @KafkaListener(topics = "booking-events", groupId = "notification-service")
    public void consumeBookingEvent(BookingEvent event) {
        log.info("Received booking event: PNR={}, Status={}", event.getPnr(), event.getStatus());
        
        if (event.getStatus() == BookingStatus.CONFIRMED) {
            notificationService.sendBookingConfirmation(event);
        } else if (event.getStatus() == BookingStatus.CANCELLED) {
            notificationService.sendBookingCancellation(event);
        }
    }
}
