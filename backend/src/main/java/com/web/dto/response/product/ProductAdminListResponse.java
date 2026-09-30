package com.web.dto.response.product;

import com.web.enums.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductAdminListResponse {

    private Long id;
    private String name;
    private Float price;
    private String thumbnail;
    private String categoryName;
    private Integer quantity;
    private Boolean status;
    private Integer discount;
    private Boolean pin;
    private Instant createdAt;
    private Instant updatedAt;
}