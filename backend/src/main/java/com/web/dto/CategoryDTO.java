package com.web.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class CategoryDTO {
    private Long id;
    private String name;
    private int quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
