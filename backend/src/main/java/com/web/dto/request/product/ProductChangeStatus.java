/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.dto.request.product;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author ZZ
 */
@Getter
@Setter
public class ProductChangeStatus {
    @NotNull(message = "Trạng thái không thể trống")
    private Boolean status;

    
    
}
