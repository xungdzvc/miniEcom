/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.dto.request.payment;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author ZZ
 */
@Getter
@Setter
public class CardCallBackRequest {
    private Long trans_id;
    private String request_id;
    private Long amount;
    private Long declared_value;
    private String telco;
    private String serial;
    private String code;
    private Integer status;
    private String message;
}
