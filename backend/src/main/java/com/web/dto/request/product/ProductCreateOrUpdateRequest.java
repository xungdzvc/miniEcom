/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.dto.request.product;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author ZZ
 */
@Getter
@Setter
public class ProductCreateOrUpdateRequest {
    private String name;
    private Long price;
    private String thumbnail;
    private String description;
    private Long userId;
    private Boolean status;
    private String slug;
    private Long categoryId;
    
    private Integer quantity;
    private String downloadUrl;
    private String youtubeUrl;
    private String demoUrl;
    private Integer discount;
    private String technology;
    private String installTutorial;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> imageUrls;
}
