package com.flightbooking.common.event;

import com.flightbooking.common.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingEvent {
    private UUID bookingId;
    private String pnr;
    private UUID userId;
    private BookingStatus status;
    private BigDecimal amount;
    private String currency;
    private String userEmail;
    private String userPhone;
    private LocalDateTime timestamp;
}
