package com.flightbooking.notification.service;

import com.flightbooking.common.event.BookingEvent;

/**
 * Service interface for sending notifications.
 */
public interface NotificationService {
    
    /**
     * Send booking confirmation notification
     */
    void sendBookingConfirmation(BookingEvent event);
    
    /**
     * Send booking cancellation notification
     */
    void sendBookingCancellation(BookingEvent event);
}
