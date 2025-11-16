package com.flightbooking.booking.dto;

import com.flightbooking.common.dto.PassengerDTO;
import com.flightbooking.common.dto.SeatSelectionDTO;
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

class DTOTest {
    
    @Test
    void testCreateBookingRequest() {
        CreateBookingRequest req = new CreateBookingRequest();
        req.setUserId(UUID.randomUUID());
        req.setItineraryId(UUID.randomUUID());
        req.setPassengers(Arrays.asList(new PassengerDTO("John", "Doe", 30, "M", null, null)));
        req.setSeats(Arrays.asList(new SeatSelectionDTO(UUID.randomUUID(), "12A")));
        
        assertEquals(1, req.getPassengers().size());
        assertEquals(1, req.getSeats().size());
    }
    
    @Test
    void testBookingResponse() {
        BookingResponse res = new BookingResponse();
        res.setBookingId(UUID.randomUUID());
        res.setPnr("ABC123");
        res.setUserId(UUID.randomUUID());
        res.setItineraryId(UUID.randomUUID());
        res.setBookingStatus(BookingStatus.CONFIRMED);
        res.setAmount(BigDecimal.valueOf(10000));
        res.setCurrency("INR");
        res.setCreatedAt(LocalDateTime.now());
        
        BookingResponse.PassengerInfo passenger = new BookingResponse.PassengerInfo();
        passenger.setFirstName("John");
        passenger.setLastName("Doe");
        passenger.setAge(30);
        passenger.setGender("M");
        res.setPassengers(Arrays.asList(passenger));
        
        BookingResponse.SeatInfo seat = new BookingResponse.SeatInfo();
        seat.setFlightId(UUID.randomUUID());
        seat.setSeatNo("12A");
        seat.setCabinClass("ECONOMY");
        res.setSeats(Arrays.asList(seat));
        
        assertEquals("ABC123", res.getPnr());
        assertEquals(1, res.getPassengers().size());
    }
    
    @Test
    void testPaymentRequest() {
        PaymentRequest req = new PaymentRequest();
        req.setBookingId(UUID.randomUUID());
        req.setGateway(PaymentGateway.RAZORPAY);
        req.setPaymentMethod(PaymentMethod.UPI);
        
        assertEquals(PaymentGateway.RAZORPAY, req.getGateway());
    }
    
    @Test
    void testPaymentResponse() {
        PaymentResponse res = new PaymentResponse();
        res.setPaymentTransactionId(UUID.randomUUID());
        res.setBookingId(UUID.randomUUID());
        res.setGateway("RAZORPAY");
        res.setGatewayTxnId("txn123");
        res.setAmount(BigDecimal.valueOf(10000));
        res.setCurrency("INR");
        res.setStatus(PaymentStatus.SUCCESS);
        res.setCreatedAt(LocalDateTime.now());
        
        assertEquals("RAZORPAY", res.getGateway());
        assertEquals(PaymentStatus.SUCCESS, res.getStatus());
    }
}
