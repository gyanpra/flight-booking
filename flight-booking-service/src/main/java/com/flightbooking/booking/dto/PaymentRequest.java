package com.flightbooking.booking.dto;

import com.flightbooking.common.enums.PaymentGateway;
import com.flightbooking.common.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    @NotNull
    private UUID bookingId;
    
    @NotNull
    private PaymentGateway gateway;
    
    @NotNull
    private PaymentMethod paymentMethod;
}
