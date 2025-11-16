package com.flightbooking.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.flightbooking.common.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private UUID paymentTransactionId;
    private UUID bookingId;
    private String gateway;
    private String gatewayTxnId;
    private BigDecimal amount;
    private String currency;
    private PaymentStatus status;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}
