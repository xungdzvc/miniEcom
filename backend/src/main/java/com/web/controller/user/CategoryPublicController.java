package com.web.controller.user;

import com.web.dto.response.common.ApiResponse;
import com.web.service.ICategoryService;
import lombok.RequiredArgsConstructor; 
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryPublicController {
    private final ICategoryService categoryService;
    @GetMapping()
    public ApiResponse<?> getAllCategoriesUser(){
        return ApiResponse.success(categoryService.getAllCategories(),"lấy danh mục thành công");
    }
}
