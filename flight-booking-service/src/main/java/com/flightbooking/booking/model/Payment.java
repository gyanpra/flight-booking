package com.flightbooking.booking.model;

import com.flightbooking.common.enums.PaymentGateway;
import com.flightbooking.common.enums.PaymentMethod;
import com.flightbooking.common.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payment_transactions", indexes = {
    @Index(name = "idx_booking_id", columnList = "booking_id"),
    @Index(name = "idx_gateway_txn", columnList = "gateway,gateway_txn_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID paymentTransactionId;
    
    @Column(nullable = false)
    private UUID bookingId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentGateway gateway;
    
    @Column(unique = true)
    private String gatewayTxnId;
    
    @Column(nullable = false)
    private BigDecimal amount;
    
    @Column(nullable = false)
    private String currency = "INR";
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.INITIATED;
    
    private String failureReason;
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime updatedAt;
}
