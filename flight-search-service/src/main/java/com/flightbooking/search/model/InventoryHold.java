package com.flightbooking.search.model;

import com.flightbooking.common.enums.HoldStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "inventory_holds", indexes = {
    @Index(name = "idx_flight_id", columnList = "flight_id"),
    @Index(name = "idx_session_id", columnList = "customer_session_id"),
    @Index(name = "idx_expires_at", columnList = "expires_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryHold {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID holdId;
    
    @Column(nullable = false)
    private UUID flightId;
    
    private UUID userId;
    
    @Column(nullable = false)
    private String customerSessionId;
    
    @Column(nullable = false)
    private Integer seatCount;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> seats;
    
    @Column(nullable = false)
    private LocalDateTime expiresAt;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HoldStatus status = HoldStatus.ACTIVE;
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
