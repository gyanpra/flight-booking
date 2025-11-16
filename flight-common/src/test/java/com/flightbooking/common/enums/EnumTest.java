package com.flightbooking.common.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnumTest {
    
    @Test
    void testBookingStatus() {
        assertEquals(6, BookingStatus.values().length);
        assertEquals(BookingStatus.CREATED, BookingStatus.valueOf("CREATED"));
    }
    
    @Test
    void testPaymentStatus() {
        assertEquals(6, PaymentStatus.values().length);
        assertEquals(PaymentStatus.SUCCESS, PaymentStatus.valueOf("SUCCESS"));
    }
    
    @Test
    void testPaymentMethod() {
        assertEquals(4, PaymentMethod.values().length);
        assertEquals(PaymentMethod.UPI, PaymentMethod.valueOf("UPI"));
    }
    
    @Test
    void testPaymentGateway() {
        assertEquals(3, PaymentGateway.values().length);
        assertEquals(PaymentGateway.RAZORPAY, PaymentGateway.valueOf("RAZORPAY"));
    }
    
    @Test
    void testCabinClass() {
        assertEquals(4, CabinClass.values().length);
        assertEquals(CabinClass.ECONOMY, CabinClass.valueOf("ECONOMY"));
    }
    
    @Test
    void testSeatStatus() {
        assertEquals(4, SeatStatus.values().length);
        assertEquals(SeatStatus.AVAILABLE, SeatStatus.valueOf("AVAILABLE"));
    }
    
    @Test
    void testHoldStatus() {
        assertEquals(4, HoldStatus.values().length);
        assertEquals(HoldStatus.ACTIVE, HoldStatus.valueOf("ACTIVE"));
    }
    
    @Test
    void testFlightStatus() {
        assertEquals(3, FlightStatus.values().length);
        assertEquals(FlightStatus.SCHEDULED, FlightStatus.valueOf("SCHEDULED"));
    }
    
    @Test
    void testCarrier() {
        assertEquals(5, Carrier.values().length);
        assertEquals(Carrier.INDIGO, Carrier.valueOf("INDIGO"));
    }
}
