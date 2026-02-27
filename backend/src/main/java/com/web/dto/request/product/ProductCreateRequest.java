package com.web.dto.request.product;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Data;

@Data
public class ProductCreateRequest {
    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;
    private Long price;
    private String thumbnail;
    private String description;
    private String installTotoirial;
    private String technolory;
    private Long categoryId;
    private Integer quantity;
    private String downloadUrl;
    private List<String> imageUrls;
    private Integer discount;
    private String slug;
    private String demoUrl;
    private String demoYoutubeUrl;
    
}
