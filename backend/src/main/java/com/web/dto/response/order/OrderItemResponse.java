package com.web.dto.response.order;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemResponse {
    private Long orderItemId;
    private Long productId;
    private String productName;
    private String categoryName;
    private String thumbnail;
    private Integer quantity;
    private Long productPrice;

}
