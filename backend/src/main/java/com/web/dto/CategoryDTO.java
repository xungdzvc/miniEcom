package com.web.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant; 
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDTO {
    @NotBlank(message="Mã Danh mục không thể để trống")
    private Long id;
    
    @NotBlank(message="Tên Danh mục không thể để trống")
    private String name;
    private Instant createdAt;
    private Instant updatedAt;
}
