/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.web.service;

import com.web.dto.request.payment.CardCallBackRequest;
import com.web.dto.request.payment.CardRequest;
import com.web.dto.request.payment.WebhookRequest;
import com.web.dto.response.common.ApiResponse;
import com.web.dto.response.payment.TopupResponse;
import com.web.dto.response.user.UserTopupResponse;
import com.web.entity.PaymentTransactionEntity;
import com.web.enums.PaymentStatus;
import java.util.List;

/**
 *
 * @author ZZ
 */
public interface IPaymentTransactionService {
    void processTransaction(WebhookRequest webhookRequest);
    void handleOrderPayment(PaymentTransactionEntity paymentTransactionEntity, Long orderId);
    void handleTopUpPayment(PaymentTransactionEntity paymentTransactionEntity, Long userId);
    ApiResponse sendCard(CardRequest cardRequest);
    ApiResponse callBack(CardCallBackRequest cardCallBackRequest);
    List<UserTopupResponse> getTopup(Long userId);
    PaymentStatus getStatusByTopupId(Long topupId);
    void handleTopUpWallet(PaymentTransactionEntity paymentTransactionEntity, Long userId);
    TopupResponse requestTopUp(Long amount);
    
    
    
}
