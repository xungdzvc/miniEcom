package com.web.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDTO {
    private Long id;
    private String name;
    private int quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
