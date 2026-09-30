/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.dto.request.order;

import com.web.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class OrderCheckoutRequest {
    @NotNull(message = "mã giỏ hàng không thể trống")
    @Positive
    private Long cartId;
    
    private String couponCode;
    
    @NotNull(message = "Phương thức thanh toán không thể để trống")
    private PaymentMethod paymentMethod;
}
