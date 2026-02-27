package com.web.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemDTO {
    private Long id;
    private Long productId;
    private int discount;
    private Long price;
    private int quantity;
}
