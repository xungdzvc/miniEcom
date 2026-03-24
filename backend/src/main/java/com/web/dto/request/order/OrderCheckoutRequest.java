/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.dto.request.order;

import com.web.enums.PaymentMethod;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class OrderCheckoutRequest {
    private Long cartId;
    private String couponCode;
    private PaymentMethod paymentMethod;
}
