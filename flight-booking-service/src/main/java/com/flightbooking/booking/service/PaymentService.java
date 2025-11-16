package com.flightbooking.booking.service;

import com.flightbooking.booking.dto.PaymentRequest;
import com.flightbooking.booking.dto.PaymentResponse;

import java.util.UUID;

/**
 * Service interface for payment operations.
 */
public interface PaymentService {
    
    /**
     * Process payment for a booking
     */
    PaymentResponse processPayment(PaymentRequest request);
    
    /**
     * Get payment details
     */
    PaymentResponse getPayment(UUID paymentId);
}
