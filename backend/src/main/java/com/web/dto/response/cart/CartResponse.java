package com.web.dto.response.cart;

import lombok.Data;

import java.util.List;

@Data
public class CartResponse {

    private long id;
    private long userId;
    private float toltalPrice;
    private List<CartItemResponse> cartItems;
    
}
