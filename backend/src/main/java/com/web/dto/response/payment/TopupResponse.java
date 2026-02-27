/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.dto.response.payment;

import com.web.enums.PaymentMethod;
import com.web.enums.PaymentStatus;
import java.time.LocalDateTime;
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
    private Long amount;
    private PaymentStatus status;
    private LocalDateTime expiresAt;
    private String QRCodeUrl;
}
