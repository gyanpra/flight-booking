package com.flightbooking.booking.service;

import com.flightbooking.booking.dto.BookingResponse;
import com.flightbooking.booking.dto.CreateBookingRequest;
import com.flightbooking.booking.mapper.BookingMapper;
import com.flightbooking.booking.model.Booking;
import com.flightbooking.booking.model.User;
import com.flightbooking.booking.repository.BookingRepository;
import com.flightbooking.booking.repository.UserRepository;
import com.flightbooking.booking.service.impl.BookingServiceImpl;
import com.flightbooking.common.dto.PassengerDTO;
import com.flightbooking.common.dto.SeatSelectionDTO;
import com.flightbooking.common.enums.BookingStatus;
import com.flightbooking.common.event.BookingEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    
    @Mock
    private BookingRepository bookingRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private BookingMapper bookingMapper;
    
    @Mock
    private KafkaTemplate<String, BookingEvent> kafkaTemplate;
    
    @InjectMocks
    private BookingServiceImpl bookingService;
    
    private User testUser;
    private Booking testBooking;
    private CreateBookingRequest createRequest;
    
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(UUID.randomUUID());
        testUser.setName("John Doe");
        testUser.setEmail("john@example.com");
        testUser.setPhone("+919876543210");
        
        testBooking = new Booking();
        testBooking.setBookingId(UUID.randomUUID());
        testBooking.setPnr("ABC123");
        testBooking.setUserId(testUser.getUserId());
        testBooking.setBookingStatus(BookingStatus.CREATED);
        
        PassengerDTO passenger = new PassengerDTO("John", "Doe", 30, "M", null, null);
        SeatSelectionDTO seat = new SeatSelectionDTO(UUID.randomUUID(), "12A");
        
        createRequest = new CreateBookingRequest();
        createRequest.setUserId(testUser.getUserId());
        createRequest.setItineraryId(UUID.randomUUID());
        createRequest.setPassengers(Arrays.asList(passenger));
        createRequest.setSeats(Arrays.asList(seat));
    }
    
    @Test
    void testCreateBooking_Success() {
        when(userRepository.findById(any())).thenReturn(Optional.of(testUser));
        when(bookingRepository.save(any())).thenReturn(testBooking);
        when(bookingMapper.toResponse(any())).thenReturn(new BookingResponse());
        
        BookingResponse response = bookingService.createBooking(createRequest);
        
        assertNotNull(response);
        verify(userRepository).findById(createRequest.getUserId());
        verify(bookingRepository).save(any(Booking.class));
        verify(kafkaTemplate).send(any(), any(), any());
    }
    
    @Test
    void testGetBooking_Success() {
        UUID bookingId = testBooking.getBookingId();
        
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));
        when(bookingMapper.toResponse(any())).thenReturn(new BookingResponse());
        
        BookingResponse response = bookingService.getBooking(bookingId);
        
        assertNotNull(response);
        verify(bookingRepository).findById(bookingId);
    }
    
    @Test
    void testGetUserBookings_Success() {
        UUID userId = testUser.getUserId();
        
        when(bookingRepository.findByUserIdOrderByCreatedAtDesc(userId))
            .thenReturn(Arrays.asList(testBooking));
        when(bookingMapper.toResponse(any())).thenReturn(new BookingResponse());
        
        List<BookingResponse> bookings = bookingService.getUserBookings(userId);
        
        assertNotNull(bookings);
        assertFalse(bookings.isEmpty());
        verify(bookingRepository).findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    @Test
    void testCancelBooking_Success() {
        UUID bookingId = testBooking.getBookingId();
        
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));
        when(bookingRepository.save(any())).thenReturn(testBooking);
        when(bookingMapper.toResponse(any())).thenReturn(new BookingResponse());
        
        BookingResponse response = bookingService.cancelBooking(bookingId);
        
        assertNotNull(response);
        verify(bookingRepository).findById(bookingId);
        verify(bookingRepository).save(any(Booking.class));
    }
}
