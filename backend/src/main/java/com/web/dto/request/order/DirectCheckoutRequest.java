/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.dto.request.order;

import com.web.enums.PaymentMethod;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author ZZ
 */
@Getter
@Setter
public class DirectCheckoutRequest {
    private Long productId;
    private Integer quantity;
    private String couponCode;
    private PaymentMethod paymentMethod;
}
