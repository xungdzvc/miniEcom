package com.web.controller.payment;

import com.web.dto.request.payment.CardCallBackRequest;
import com.web.dto.request.payment.CardRequest;
import com.web.dto.request.payment.TopupRequest;
import com.web.dto.request.payment.WebhookRequest;
import com.web.dto.response.common.ApiResponse;
import com.web.security.ratelimit.RateLimited;
import com.web.service.IPaymentTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.MessageDigest;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaymentController {

    @Value("${webhook.apiKey}")
    private String apiKey;

    private final IPaymentTransactionService paymentService;

    @PostMapping("/webhook")
    public ApiResponse<?> webhook(
            @RequestHeader(value = "authorization", required = false) String authorization,
            @RequestBody WebhookRequest webhookRequest) {

        if (authorization == null || !isValidApiKey(authorization)) {
            return ApiResponse.error(HttpStatus.UNAUTHORIZED.name());
        }
        paymentService.processTransaction(webhookRequest);
        return ApiResponse.success(webhookRequest);
    }

    @PostMapping("/charging")
    public ApiResponse<?> charging(@Valid @RequestBody CardRequest cardRequest) {
        return paymentService.sendCard(cardRequest);
    }

    @RateLimited("topup-create")
    @PostMapping("/topup")
    public ApiResponse<?> topup(@Valid @RequestBody TopupRequest request) {
        return ApiResponse.success(paymentService.requestTopUp(request));
    }

    @GetMapping("/topup/status/{topupId}")
    public ApiResponse<?> getTopupStatus(@PathVariable Long topupId) {
        return ApiResponse.success(paymentService.getTopupStatus(topupId));
    }

    @PostMapping("/callback")
    public ApiResponse<?> callback(@Valid @RequestBody CardCallBackRequest request) {
        return paymentService.handleCardCallback(request);
    }

    private boolean isValidApiKey(String authorization) {
        try {
            String key = authorization.replace("Apikey ", "");
            return MessageDigest.isEqual(key.getBytes(), apiKey.getBytes());
        } catch (Exception e) {
            return false;
        }
    }
}
