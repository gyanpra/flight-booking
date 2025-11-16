package com.flightbooking.booking.model;

import com.flightbooking.common.enums.BookingStatus;
import com.flightbooking.common.enums.PaymentGateway;
import com.flightbooking.common.enums.PaymentMethod;
import com.flightbooking.common.enums.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest {
    
    @Test
    void testUser() {
        User user = new User();
        user.setUserId(UUID.randomUUID());
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPhone("+919876543210");
        
        assertEquals("John Doe", user.getName());
        assertEquals("john@example.com", user.getEmail());
    }
    
    @Test
    void testBooking() {
        Booking booking = new Booking();
        booking.setBookingId(UUID.randomUUID());
        booking.setPnr("ABC123");
        booking.setUserId(UUID.randomUUID());
        booking.setItineraryId(UUID.randomUUID());
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        booking.setAmount(BigDecimal.valueOf(10000));
        booking.setCurrency("INR");
        booking.setCreatedAt(LocalDateTime.now());
        
        Booking.PassengerInfo passenger = new Booking.PassengerInfo();
        passenger.setFirstName("John");
        passenger.setLastName("Doe");
        passenger.setAge(30);
        passenger.setGender("M");
        booking.setPassengers(Arrays.asList(passenger));
        
        Booking.SeatInfo seat = new Booking.SeatInfo();
        seat.setFlightId(UUID.randomUUID());
        seat.setSeatNo("12A");
        seat.setCabinClass("ECONOMY");
        booking.setSeats(Arrays.asList(seat));
        
        assertEquals("ABC123", booking.getPnr());
        assertEquals(1, booking.getPassengers().size());
        assertEquals("John", booking.getPassengers().get(0).getFirstName());
    }
    
    @Test
    void testPayment() {
        Payment payment = new Payment();
        payment.setPaymentTransactionId(UUID.randomUUID());
        payment.setBookingId(UUID.randomUUID());
        payment.setGateway(PaymentGateway.RAZORPAY);
        payment.setGatewayTxnId("txn123");
        payment.setAmount(BigDecimal.valueOf(10000));
        payment.setCurrency("INR");
        payment.setPaymentMethod(PaymentMethod.UPI);
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setCreatedAt(LocalDateTime.now());
        
        assertEquals(PaymentGateway.RAZORPAY, payment.getGateway());
        assertEquals(PaymentStatus.SUCCESS, payment.getStatus());
    }
}
