/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.dto.response.payment;
 
import com.web.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author ZZ
 */
@Setter
@Getter
public class TopupResponse {
    private Long topupId;
    private BigDecimal amount;
    private PaymentStatus status;
    private Instant expiresAt;
    private String QRCodeUrl;
}
