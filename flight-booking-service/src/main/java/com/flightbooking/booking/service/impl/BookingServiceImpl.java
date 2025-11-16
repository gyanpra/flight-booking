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
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;
    private final KafkaTemplate<String, BookingEvent> kafkaTemplate;
    private final RedissonClient redissonClient;
    
    private static final String BOOKING_EVENTS_TOPIC = "booking-events";
    
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Retryable(
        retryFor = {OptimisticLockException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 100, multiplier = 2)
    )
    @RateLimiter(name = "bookingRateLimiter", fallbackMethod = "createBookingFallback")
    @CircuitBreaker(name = "bookingCircuitBreaker", fallbackMethod = "createBookingFallback")
    public BookingResponse createBooking(CreateBookingRequest request) {
        String lockKey = "booking:user:" + request.getUserId();
        RLock lock = redissonClient.getLock(lockKey);
        
        try {
            if (lock.tryLock(5, 15, TimeUnit.SECONDS)) {
                try {
                    log.info("Creating booking for user: {}", request.getUserId());
                    
                    User user = userRepository.findById(request.getUserId())
                        .orElseThrow(() -> new ResourceNotFoundException("User not found: " + request.getUserId()));
                    
                    return createBookingInternal(request, user);
                } finally {
                    lock.unlock();
                }
            } else {
                throw new BusinessException("Unable to acquire lock for booking creation");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("Booking creation interrupted");
        }
    }
    
    private BookingResponse createBookingInternal(CreateBookingRequest request, User user) {
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
    
    private BookingResponse createBookingFallback(CreateBookingRequest request, Exception e) {
        log.error("Booking creation failed for user: {}", request.getUserId(), e);
        throw new BusinessException("Booking service temporarily unavailable. Please try again.");
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
