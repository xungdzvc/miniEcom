/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.controller.payment;

import com.web.dto.request.payment.CardCallBackRequest;
import com.web.dto.request.payment.CardRequest;
import com.web.dto.request.payment.TopupRequest;
import com.web.dto.request.payment.WebhookRequest;
import com.web.dto.response.common.ApiResponse;
import com.web.service.IPaymentTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author ZZ
 */
@RestController
@RequestMapping("/api")
@Configuration
@RequiredArgsConstructor
public class PaymentController {

    @Value("${webhook.apiKey}")
    private String API_KEY;

    private final IPaymentTransactionService paymentTransactionService;

    @PostMapping("/webhook")
    public ApiResponse<?> webhook(@RequestHeader(value = "authorization", required = false) String authorization, @RequestBody WebhookRequest webhookRequest) {
        if (authorization == null || !authorization.replace("Apikey ", "").equals(API_KEY)) {
            return ApiResponse.error(HttpStatus.UNAUTHORIZED.name());
        }
        paymentTransactionService.processTransaction(webhookRequest);

        return ApiResponse.success(webhookRequest);

    }

    @PostMapping("/charging")
    public ApiResponse<?> charging(@RequestBody CardRequest cardRequest) {
        return paymentTransactionService.sendCard(cardRequest);
    }
    @PostMapping("/topup")
    public ApiResponse<?> topup(@RequestBody TopupRequest topupBody){
        return ApiResponse.success(paymentTransactionService.requestTopUp(topupBody.getAmount()));
    }
    
    @GetMapping("/topup/status/{topupId}")
    public ApiResponse<?> getStatusByTopupId(@PathVariable Long topupId){
        return ApiResponse.success(paymentTransactionService.getStatusByTopupId(topupId));
    }
    
    @PostMapping("/callback")
    public ApiResponse<?> callback(@RequestBody CardCallBackRequest cardCallBackRequest) {
        return paymentTransactionService.callBack(cardCallBackRequest);

    }
}
