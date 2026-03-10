package com.web.dto.response.product;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductViewerListResponse {

    private Long id;
    private String name;
    private Float price;
    private String categoryName;
    private String description;
    private String thumbnail;
    private Long viewCount;
    private Long saleCount;
    private Integer discount;
    private String slug;
    private Boolean pin;
}