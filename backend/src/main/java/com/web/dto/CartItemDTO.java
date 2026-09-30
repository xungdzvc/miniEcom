package com.web.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemDTO {
    private Long id;
    private Long productId;
    private int discount;
    private BigDecimal price;
    private int quantity;
}
