package com.web.dto;

import lombok.Data;

import java.util.Date;

@Data
public class CategoryDTO {
    private Long id;
    private String name;
    private int quantity;
    private Date createdAt;
    private Date updatedAt;
}
