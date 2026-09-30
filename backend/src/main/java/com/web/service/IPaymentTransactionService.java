package com.web.service;

import com.web.dto.request.payment.CardCallBackRequest;
import com.web.dto.request.payment.CardRequest;
import com.web.dto.request.payment.TopupRequest;
import com.web.dto.request.payment.WebhookRequest;
import com.web.dto.response.common.ApiResponse;
import com.web.dto.response.payment.OrderPaymentResponse;
import com.web.dto.response.payment.TopupPaymentResponse;
import com.web.dto.response.payment.TopupResponse;
import com.web.enums.PaymentStatus;

import java.util.List;

public interface IPaymentTransactionService {

    /* ==================== BANK WEBHOOK ==================== */
    void processTransaction(WebhookRequest webhookRequest);

    /* ==================== ORDER PAYMENT ==================== */
    OrderPaymentResponse handleBankOrderPayment(WebhookRequest webhookRequest, Long orderId);

    /* ==================== TOPUP PAYMENT ==================== */
    TopupResponse requestTopUp(TopupRequest topupRequest);
    TopupPaymentResponse handleBankTopupPayment(WebhookRequest webhookRequest, Long topupId);

    /* ==================== CARD CHARGING ==================== */
    ApiResponse<?> sendCard(CardRequest cardRequest);
    ApiResponse<?> handleCardCallback(CardCallBackRequest cardCallBackRequest);

    /* ==================== QUERY ==================== */
    List<TopupPaymentResponse> getUserTopupHistory(Long userId);
    PaymentStatus getTopupStatus(Long topupId);
    List<OrderPaymentResponse> getOrderPayments(Long orderId);
}
