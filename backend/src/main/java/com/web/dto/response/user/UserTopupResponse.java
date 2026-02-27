/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.dto.response.user;

import com.web.enums.PaymentStatus;
import com.web.enums.PaymentType;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author ZZ
 */
@Getter
@Setter
public class UserTopupResponse {
    private Long Id;
    private PaymentType paymentType;
    private PaymentStatus paymentStatus;
    private String cardType;
    private String cardCode;
    private String cardSerial;
    private LocalDateTime createdAt; 
    private Long amount;
}
