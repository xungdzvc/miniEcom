package com.web.dto.response.payment;

import com.web.enums.PaymentMethod;
import com.web.enums.PaymentStatus;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class OrderPaymentResponse {
    private Long id;
    private Long orderId;
    private Long transactionId;
    private String transferContent;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private Instant createdAt;
}
