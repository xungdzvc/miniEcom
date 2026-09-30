package com.web.dto.response.payment;

import com.web.enums.PaymentMethod;
import com.web.enums.PaymentStatus;
import com.web.enums.PaymentType;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class TopupPaymentResponse {
    private Long id;
    private Long userId;
    private Long transactionId;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private String cardType;
    private String cardCode;
    private String cardSerial;
    private PaymentStatus status;
    private PaymentType paymentType;
    private Instant createdAt;
}
