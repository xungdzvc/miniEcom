package com.web.dto.request.category;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CategoryCreateRequest {
    private Long parentId;

    @NotBlank(message="Tên Danh mục không thể để trống")
    private String name;

}
