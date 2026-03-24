package com.web.dto.response.product;

 

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
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
    private String downloadUrl;
    private Integer viewCount;
    private Integer saleCount;
    private Integer discount;
    private String technology;
    private String installTutorial;
    private Boolean pin;
    private String shareBy;
    private List<String> imageUrls;
}
