package com.flightbooking.notification.service.impl;

import com.flightbooking.common.enums.BookingStatus;
import com.flightbooking.common.event.BookingEvent;
import com.flightbooking.notification.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    
    @Override
    public void sendBookingConfirmation(BookingEvent event) {
        log.info("=== SENDING BOOKING CONFIRMATION ===");
        log.info("PNR: {}", event.getPnr());
        log.info("User Email: {}", event.getUserEmail());
        log.info("User Phone: {}", event.getUserPhone());
        log.info("Amount: {} {}", event.getAmount(), event.getCurrency());
        log.info("Status: {}", event.getStatus());
        log.info("===================================");
        
        // In production: integrate with email/SMS service
        // Example: emailService.send(event.getUserEmail(), "Booking Confirmed", buildEmailBody(event));
        // Example: smsService.send(event.getUserPhone(), buildSmsMessage(event));
    }
    
    @Override
    public void sendBookingCancellation(BookingEvent event) {
        log.info("=== SENDING BOOKING CANCELLATION ===");
        log.info("PNR: {}", event.getPnr());
        log.info("User Email: {}", event.getUserEmail());
        log.info("User Phone: {}", event.getUserPhone());
        log.info("====================================");
        
        // In production: integrate with email/SMS service
    }
}
