package com.web.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ProductDTO {

    private Long id;
    private String name;
    private float price;
    private String thumbnail;
    private Boolean status;

    // Category
    private Long categoryId;
    private String categoryName;

    // User
    private Long userId;
    private String username;

    // Product Details
    private Integer quantity;
    private String description;
    private String linkDownload;
    private String linkDemoYoutube;
    private String linkDemo;
    private Integer viewCount;
    private Integer saleCount;

    // Product Images
    private List<String> productImages;

    private Date createdAt;
    private Date updatedAt;
}
