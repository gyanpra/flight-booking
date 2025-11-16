package com.flightbooking.booking.service.impl;

import com.flightbooking.booking.dto.PaymentRequest;
import com.flightbooking.booking.dto.PaymentResponse;
import com.flightbooking.booking.mapper.PaymentMapper;
import com.flightbooking.booking.model.Booking;
import com.flightbooking.booking.model.Payment;
import com.flightbooking.booking.model.User;
import com.flightbooking.booking.repository.BookingRepository;
import com.flightbooking.booking.repository.PaymentRepository;
import com.flightbooking.booking.repository.UserRepository;
import com.flightbooking.booking.service.PaymentService;
import com.flightbooking.common.enums.BookingStatus;
import com.flightbooking.common.enums.PaymentStatus;
import com.flightbooking.common.event.BookingEvent;
import com.flightbooking.common.exception.PaymentException;
import com.flightbooking.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final PaymentMapper paymentMapper;
    private final KafkaTemplate<String, BookingEvent> kafkaTemplate;
    
    private static final String BOOKING_EVENTS_TOPIC = "booking-events";
    
    @Override
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        log.info("Processing payment for booking: {}", request.getBookingId());
        
        Booking booking = bookingRepository.findById(request.getBookingId())
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + request.getBookingId()));
        
        if (booking.getBookingStatus() == BookingStatus.CONFIRMED) {
            throw new PaymentException("Booking already confirmed");
        }
        
        Payment payment = new Payment();
        payment.setBookingId(request.getBookingId());
        payment.setGateway(request.getGateway());
        payment.setGatewayTxnId(UUID.randomUUID().toString()); // Simulated
        payment.setAmount(booking.getAmount());
        payment.setCurrency(booking.getCurrency());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setStatus(PaymentStatus.SUCCESS); // Simulated success
        payment.setUpdatedAt(LocalDateTime.now());
        
        payment = paymentRepository.save(payment);
        
        // Update booking status
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        booking.setPaymentTransactionId(payment.getPaymentTransactionId());
        booking.setUpdatedAt(LocalDateTime.now());
        bookingRepository.save(booking);
        
        // Publish confirmed booking event
        User user = userRepository.findById(booking.getUserId()).orElse(null);
        if (user != null) {
            publishConfirmedBookingEvent(booking, user);
        }
        
        log.info("Payment processed successfully for booking: {}", booking.getPnr());
        return paymentMapper.toResponse(payment);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + paymentId));
        return paymentMapper.toResponse(payment);
    }
    
    private void publishConfirmedBookingEvent(Booking booking, User user) {
        BookingEvent event = new BookingEvent();
        event.setBookingId(booking.getBookingId());
        event.setPnr(booking.getPnr());
        event.setUserId(booking.getUserId());
        event.setStatus(BookingStatus.CONFIRMED);
        event.setAmount(booking.getAmount());
        event.setCurrency(booking.getCurrency());
        event.setUserEmail(user.getEmail());
        event.setUserPhone(user.getPhone());
        event.setTimestamp(LocalDateTime.now());
        
        kafkaTemplate.send(BOOKING_EVENTS_TOPIC, booking.getBookingId().toString(), event);
        log.info("Published confirmed booking event for: {}", booking.getPnr());
    }
}
