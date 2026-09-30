/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.dto.request.payment;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

    @NotNull(message = "Giá trị không được để trống")
    @Positive(message = "Giá trị phải lớn hơn 0")
    private BigDecimal amount;

    private BigDecimal declared_value;

    private String telco;
    private String serial;
    private String code;
    private Integer status;
    private String message;
}
