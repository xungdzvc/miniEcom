/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.dto.response.cart;

import java.util.List;
import lombok.Data;

/**
 *
 * @author ZZ
 */
@Data
public class CartItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private Long price;
    private String categoryName;
    private String thumbnail;
    private Integer discount;
    private Integer quantity;
    private String slug;
   
}
