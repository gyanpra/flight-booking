package com.flightbooking.booking.service;

import com.flightbooking.booking.dto.PaymentRequest;
import com.flightbooking.booking.dto.PaymentResponse;
import com.flightbooking.booking.mapper.PaymentMapper;
import com.flightbooking.booking.model.Booking;
import com.flightbooking.booking.model.Payment;
import com.flightbooking.booking.model.User;
import com.flightbooking.booking.repository.BookingRepository;
import com.flightbooking.booking.repository.PaymentRepository;
import com.flightbooking.booking.repository.UserRepository;
import com.flightbooking.booking.service.impl.PaymentServiceImpl;
import com.flightbooking.common.enums.BookingStatus;
import com.flightbooking.common.enums.PaymentGateway;
import com.flightbooking.common.enums.PaymentMethod;
import com.flightbooking.common.enums.PaymentStatus;
import com.flightbooking.common.event.BookingEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    
    @Mock
    private PaymentRepository paymentRepository;
    
    @Mock
    private BookingRepository bookingRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PaymentMapper paymentMapper;
    
    @Mock
    private KafkaTemplate<String, BookingEvent> kafkaTemplate;
    
    @Mock
    private org.redisson.api.RedissonClient redissonClient;
    
    @InjectMocks
    private PaymentServiceImpl paymentService;
    
    private Booking testBooking;
    private Payment testPayment;
    private User testUser;
    
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(UUID.randomUUID());
        testUser.setEmail("john@example.com");
        testUser.setPhone("+919876543210");
        
        testBooking = new Booking();
        testBooking.setBookingId(UUID.randomUUID());
        testBooking.setPnr("ABC123");
        testBooking.setUserId(testUser.getUserId());
        testBooking.setBookingStatus(BookingStatus.CREATED);
        testBooking.setAmount(BigDecimal.valueOf(10000));
        testBooking.setCurrency("INR");
        
        testPayment = new Payment();
        testPayment.setPaymentTransactionId(UUID.randomUUID());
        testPayment.setBookingId(testBooking.getBookingId());
        testPayment.setGateway(PaymentGateway.RAZORPAY);
        testPayment.setAmount(testBooking.getAmount());
        testPayment.setStatus(PaymentStatus.SUCCESS);
    }
    
    @Test
    void testProcessPayment_Success() throws InterruptedException {
        PaymentRequest request = new PaymentRequest();
        request.setBookingId(testBooking.getBookingId());
        request.setGateway(PaymentGateway.RAZORPAY);
        request.setPaymentMethod(PaymentMethod.UPI);
        
        org.redisson.api.RLock mockLock = mock(org.redisson.api.RLock.class);
        when(redissonClient.getLock(any())).thenReturn(mockLock);
        when(mockLock.tryLock(anyLong(), anyLong(), any())).thenReturn(true);
        when(bookingRepository.findById(any())).thenReturn(Optional.of(testBooking));
        when(paymentRepository.save(any())).thenReturn(testPayment);
        when(userRepository.findById(any())).thenReturn(Optional.of(testUser));
        when(paymentMapper.toResponse(any())).thenReturn(new PaymentResponse());
        
        PaymentResponse response = paymentService.processPayment(request);
        
        assertNotNull(response);
        verify(bookingRepository).findById(request.getBookingId());
        verify(paymentRepository).save(any(Payment.class));
        verify(bookingRepository).save(any(Booking.class));
        verify(kafkaTemplate).send(any(), any(), any());
    }
    
    @Test
    void testGetPayment_Success() {
        UUID paymentId = testPayment.getPaymentTransactionId();
        
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(testPayment));
        when(paymentMapper.toResponse(any())).thenReturn(new PaymentResponse());
        
        PaymentResponse response = paymentService.getPayment(paymentId);
        
        assertNotNull(response);
        verify(paymentRepository).findById(paymentId);
    }
}
