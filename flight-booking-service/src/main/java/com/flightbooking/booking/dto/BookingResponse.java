package com.flightbooking.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.flightbooking.common.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private UUID bookingId;
    private String pnr;
    private UUID userId;
    private UUID itineraryId;
    private BookingStatus bookingStatus;
    private BigDecimal amount;
    private String currency;
    private List<PassengerInfo> passengers;
    private List<SeatInfo> seats;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expiresAt;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PassengerInfo {
        private String firstName;
        private String lastName;
        private Integer age;
        private String gender;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SeatInfo {
        private UUID flightId;
        private String seatNo;
        private String cabinClass;
    }
}
