package com.flightbooking.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightbooking.booking.dto.BookingResponse;
import com.flightbooking.booking.dto.CreateBookingRequest;
import com.flightbooking.booking.service.BookingService;
import com.flightbooking.common.dto.PassengerDTO;
import com.flightbooking.common.dto.SeatSelectionDTO;
import com.flightbooking.common.enums.BookingStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private BookingService bookingService;
    
    @Test
    void testCreateBooking() throws Exception {
        CreateBookingRequest request = new CreateBookingRequest();
        request.setUserId(UUID.randomUUID());
        request.setItineraryId(UUID.randomUUID());
        request.setPassengers(Arrays.asList(new PassengerDTO("John", "Doe", 30, "M", null, null)));
        request.setSeats(Arrays.asList(new SeatSelectionDTO(UUID.randomUUID(), "12A")));
        
        BookingResponse response = new BookingResponse();
        response.setBookingId(UUID.randomUUID());
        response.setPnr("ABC123");
        response.setBookingStatus(BookingStatus.CREATED);
        response.setAmount(BigDecimal.valueOf(10000));
        
        when(bookingService.createBooking(any())).thenReturn(response);
        
        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pnr").value("ABC123"));
    }
    
    @Test
    void testGetBooking() throws Exception {
        UUID bookingId = UUID.randomUUID();
        BookingResponse response = new BookingResponse();
        response.setBookingId(bookingId);
        response.setPnr("ABC123");
        
        when(bookingService.getBooking(bookingId)).thenReturn(response);
        
        mockMvc.perform(get("/api/bookings/" + bookingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pnr").value("ABC123"));
    }
}
