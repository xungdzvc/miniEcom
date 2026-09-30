package com.web.dto.request.payment;

import com.web.enums.PaymentStatus;

import java.math.BigDecimal;

public record CardSubmissResponse(Long transactionId, PaymentStatus paymentStatus, BigDecimal amount,String message) {
}
