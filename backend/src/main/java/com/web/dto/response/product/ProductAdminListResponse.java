package com.web.dto.response.product;

import com.web.enums.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}