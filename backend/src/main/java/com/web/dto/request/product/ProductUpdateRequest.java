/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.dto.request.product;

import java.util.List;
import lombok.Data;

/**
 *
 * @author ZZ
 */
@Data
public class ProductUpdateRequest {

    private Long id;
    private String name;
    private Long price;
    private String thumbnail;
    private String description;
    private boolean status;
    private Long categoryId;
    private Integer quantity;
    private Integer discount;

    private String installTotoirial;
    private String demoUrl;
    private String downloadUrl;
    private String youtubeUrl;

    private List<String> imageUrls;
}
