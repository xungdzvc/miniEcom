package com.web.dto.response.product;

import com.web.dto.request.product.ProductDetailDTO;
import com.web.enums.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ProductResponse {

    private Long id;
    private String name;
    private Long price;
    private String thumbnail;
    private String description;
    private Long userId;
    private Boolean status;
    private String slug;
    private Long categoryId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer quantity;
    private String youtubeUrl;
    private String demoUrl;
    private Integer viewCount;
    private Integer saleCount;
    private Integer discount;
    private String technology;
    private String installTutorial;
    private List<String> imageUrls;
}
