/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.dto.request.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author ZZ
 */
@Setter
@Getter
public class CartItemQuantityRequest {
    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng không được nhỏ hơn 1")
    private Integer quantity;
}
