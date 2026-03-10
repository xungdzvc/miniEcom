/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.dto.request.product;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author ZZ
 */
@Getter
@Setter
public class ProductDetailDTO {
    private Long id;
    private Long productId;
    private Integer quantity;
    private String downloadUrl;
    private String youtubeUrl;
    private String demoUrl;
    private Integer viewCount;
    private Integer saleCount;
    private Integer discount;
    private String technology;
    private String installTutorial;
    private Boolean pin;
    private String shareBy;
}
