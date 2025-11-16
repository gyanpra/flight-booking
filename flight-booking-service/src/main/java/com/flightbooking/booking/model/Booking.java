package com.flightbooking.booking.model;

import com.flightbooking.common.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "bookings", indexes = {
    @Index(name = "idx_user_created", columnList = "user_id,created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID bookingId;
    
    @Column(nullable = false, unique = true)
    private String pnr;
    
    @Column(nullable = false)
    private UUID userId;
    
    @Column(nullable = false)
    private UUID itineraryId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus bookingStatus = BookingStatus.CREATED;
    
    @Column(nullable = false)
    private BigDecimal amount;
    
    @Column(nullable = false)
    private String currency = "INR";
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<PassengerInfo> passengers;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<SeatInfo> seats;
    
    private UUID holdId;
    
    private UUID paymentTransactionId;
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime expiresAt;
    
    private LocalDateTime updatedAt;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PassengerInfo {
        private String firstName;
        private String lastName;
        private Integer age;
        private String gender;
        private String documentType;
        private String documentNumber;
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
