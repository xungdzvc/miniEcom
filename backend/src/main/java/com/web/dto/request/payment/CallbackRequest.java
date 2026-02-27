/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.dto.request.payment;

import lombok.Data;

import java.time.LocalDateTime;

/**
 *
 * @author ZZ
 */
@Data
public class CallbackRequest {
    private Long id;
    private String gateway;
    private String transactionDate;
    private String accountNumber;
    private String subAccount;
    private String code;
    private String content;
    private String transferType;
    private String description;
    private String transferAmount;
    private String referenceCode;
    private String accumulated;
    
}
