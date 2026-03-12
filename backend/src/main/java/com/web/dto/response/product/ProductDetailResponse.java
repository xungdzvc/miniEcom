/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.dto.response.product;

import java.util.List;
import lombok.Data;

/**
 *
 * @author ZZ
 */
@Data
public class ProductDetailResponse {
    private Long id;
    private String name;
    private float price;

    private Long categoryId;
    private String categoryName;
    private Integer quantity;
    private String thumbnail;
    private String description;
    private String youtubeUrl;
    private String demoUrl;
    private Integer viewCount;
    private Integer saleCount;
    private Integer discount;
    private String technology;
    private String installTutorial;
    private Boolean pin;
    private String shareBy;
    private List<String> imageUrls;
}
