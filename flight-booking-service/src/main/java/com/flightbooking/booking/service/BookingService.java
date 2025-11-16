package com.flightbooking.booking.service;

import com.flightbooking.booking.dto.BookingResponse;
import com.flightbooking.booking.dto.CreateBookingRequest;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for booking operations.
 */
public interface BookingService {
    
    /**
     * Create a new booking
     */
    BookingResponse createBooking(CreateBookingRequest request);
    
    /**
     * Get booking by ID
     */
    BookingResponse getBooking(UUID bookingId);
    
    /**
     * Get all bookings for a user
     */
    List<BookingResponse> getUserBookings(UUID userId);
    
    /**
     * Cancel a booking
     */
    BookingResponse cancelBooking(UUID bookingId);
}
