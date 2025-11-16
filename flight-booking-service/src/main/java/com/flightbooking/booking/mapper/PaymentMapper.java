package com.flightbooking.booking.mapper;

import com.flightbooking.booking.dto.PaymentResponse;
import com.flightbooking.booking.model.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {
    
    public PaymentResponse toResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setPaymentTransactionId(payment.getPaymentTransactionId());
        response.setBookingId(payment.getBookingId());
        response.setGateway(payment.getGateway().name());
        response.setGatewayTxnId(payment.getGatewayTxnId());
        response.setAmount(payment.getAmount());
        response.setCurrency(payment.getCurrency());
        response.setStatus(payment.getStatus());
        response.setCreatedAt(payment.getCreatedAt());
        return response;
    }
}
