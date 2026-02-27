/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.dto.response.product;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author ZZ
 */

@Setter
@Getter
public class ProductImageDTO {
    private Long id;
    private Long productId;
    private Long imageUrl;
}
