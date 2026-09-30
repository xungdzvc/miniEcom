/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.dto.response.coupon;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author ZZ
 */
@Getter
@Setter
public class CouponResponse {
    private Long id;
    private String couponCode;
    private Integer discount;
    private Instant createdAt;
    private Instant updatedAt;
}
