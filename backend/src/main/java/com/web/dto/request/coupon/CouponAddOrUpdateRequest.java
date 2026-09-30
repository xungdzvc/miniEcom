/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.dto.request.coupon;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author ZZ
 */
@Getter
@Setter
public class CouponAddOrUpdateRequest {
    
    @NotBlank(message = "Mã giảm giá không được để trống")
    @Size(min = 3 , max = 50, message = "Mã giảm giá phải từ 3-50 ký tự")
    private String couponCode;

    @NotNull
    @Min(value = 1,message = "Giảm giá không được nhỏ hơn 1")
    @Max(value = 100,message =  "Giảm giá không được lớn hơn 100")
    private Integer discount;
}
