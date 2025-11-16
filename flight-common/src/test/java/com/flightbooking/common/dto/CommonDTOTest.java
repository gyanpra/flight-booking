package com.flightbooking.common.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CommonDTOTest {
    
    @Test
    void testPassengerDTO() {
        PassengerDTO dto = new PassengerDTO();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setAge(30);
        dto.setGender("M");
        dto.setDocumentType("PASSPORT");
        dto.setDocumentNumber("AB123456");
        
        assertEquals("John", dto.getFirstName());
        assertEquals(30, dto.getAge());
    }
    
    @Test
    void testSeatSelectionDTO() {
        SeatSelectionDTO dto = new SeatSelectionDTO();
        dto.setFlightId(UUID.randomUUID());
        dto.setSeatNo("12A");
        
        assertEquals("12A", dto.getSeatNo());
    }
    
    @Test
    void testErrorResponse() {
        ErrorResponse error = new ErrorResponse();
        error.setTimestamp(LocalDateTime.now());
        error.setStatus(404);
        error.setError("Not Found");
        error.setMessage("Resource not found");
        error.setPath("/api/test");
        
        assertEquals(404, error.getStatus());
        assertEquals("Not Found", error.getError());
    }
}
