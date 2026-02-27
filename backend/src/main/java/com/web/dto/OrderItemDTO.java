package com.web.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemDTO {
    private Long id;
    private Long orderId;
    private Long productId;
    private Long quantity;
    private Float price;

}
