/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.dto.response.category;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author ZZ
 */
@Getter
@Setter
public class CategoryResponse {
    private Long id;
    private Long parentId;
    private String name;
    private Instant createdAt;
    private Instant updatedAt;
}
