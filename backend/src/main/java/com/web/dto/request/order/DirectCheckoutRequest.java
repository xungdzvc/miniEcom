/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.dto.request.order;

import com.web.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

    @NotNull(message = "mã sản phẩm không thể trống")
    @Positive(message = "mã sản phẩm không hợp lệ")
    private Long productId;

    @NotNull(message = "Số lượng phải lớn hơn 0")
    @Positive
    private Integer quantity;

    private String couponCode;

    @NotNull(message = "Phương thức thanh toán không thể để trống")
    private PaymentMethod paymentMethod;
}
