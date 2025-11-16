package com.flightbooking.booking.service.impl;

import com.flightbooking.booking.dto.BookingResponse;
import com.flightbooking.booking.dto.CreateBookingRequest;
import com.flightbooking.booking.mapper.BookingMapper;
import com.flightbooking.booking.model.Booking;
import com.flightbooking.booking.model.User;
import com.flightbooking.booking.repository.BookingRepository;
import com.flightbooking.booking.repository.UserRepository;
import com.flightbooking.booking.service.BookingService;
import com.flightbooking.common.enums.BookingStatus;
import com.flightbooking.common.event.BookingEvent;
import com.flightbooking.common.exception.BusinessException;
import com.flightbooking.common.exception.ResourceNotFoundException;
import com.flightbooking.common.util.PNRGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;
    private final KafkaTemplate<String, BookingEvent> kafkaTemplate;
    
    private static final String BOOKING_EVENTS_TOPIC = "booking-events";
    
    @Override
    @Transactional
    public BookingResponse createBooking(CreateBookingRequest request) {
        log.info("Creating booking for user: {}", request.getUserId());
        
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + request.getUserId()));
        
        Booking booking = new Booking();
        booking.setPnr(PNRGenerator.generate());
        booking.setUserId(request.getUserId());
        booking.setItineraryId(request.getItineraryId());
        booking.setBookingStatus(BookingStatus.CREATED);
        booking.setAmount(calculateAmount(request));
        booking.setCurrency("INR");
        booking.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        
        List<Booking.PassengerInfo> passengers = request.getPassengers().stream()
            .map(p -> new Booking.PassengerInfo(
                p.getFirstName(), p.getLastName(), p.getAge(), 
                p.getGender(), p.getDocumentType(), p.getDocumentNumber()
            ))
            .collect(Collectors.toList());
        booking.setPassengers(passengers);
        
        List<Booking.SeatInfo> seats = request.getSeats().stream()
            .map(s -> new Booking.SeatInfo(s.getFlightId(), s.getSeatNo(), "ECONOMY"))
            .collect(Collectors.toList());
        booking.setSeats(seats);
        
        booking = bookingRepository.save(booking);
        
        publishBookingEvent(booking, user);
        
        log.info("Booking created successfully: {}", booking.getPnr());
        return bookingMapper.toResponse(booking);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));
        return bookingMapper.toResponse(booking);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getUserBookings(UUID userId) {
        List<Booking> bookings = bookingRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return bookings.stream()
            .map(bookingMapper::toResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public BookingResponse cancelBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));
        
        if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new BusinessException("Booking already cancelled");
        }
        
        booking.setBookingStatus(BookingStatus.CANCELLED);
        booking.setUpdatedAt(LocalDateTime.now());
        booking = bookingRepository.save(booking);
        
        log.info("Booking cancelled: {}", booking.getPnr());
        return bookingMapper.toResponse(booking);
    }
    
    private BigDecimal calculateAmount(CreateBookingRequest request) {
        // Simplified calculation - in production, fetch actual prices
        return BigDecimal.valueOf(5000).multiply(BigDecimal.valueOf(request.getPassengers().size()));
    }
    
    private void publishBookingEvent(Booking booking, User user) {
        BookingEvent event = new BookingEvent();
        event.setBookingId(booking.getBookingId());
        event.setPnr(booking.getPnr());
        event.setUserId(booking.getUserId());
        event.setStatus(booking.getBookingStatus());
        event.setAmount(booking.getAmount());
        event.setCurrency(booking.getCurrency());
        event.setUserEmail(user.getEmail());
        event.setUserPhone(user.getPhone());
        event.setTimestamp(LocalDateTime.now());
        
        kafkaTemplate.send(BOOKING_EVENTS_TOPIC, booking.getBookingId().toString(), event);
        log.info("Published booking event for: {}", booking.getPnr());
    }
}
