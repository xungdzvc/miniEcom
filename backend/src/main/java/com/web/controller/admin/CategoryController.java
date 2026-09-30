package com.web.controller.admin;

import com.web.dto.request.category.CategoryCreateRequest;
import com.web.dto.request.category.CategoryUpdateRequest;
import com.web.dto.response.common.ApiResponse;
import com.web.service.ICategoryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final ICategoryService categoryService;

    @PostMapping()
    public ApiResponse<?> addCategory(@Valid @RequestBody CategoryCreateRequest req) {

        return ApiResponse.success(categoryService.createCategory(req), "Tạo danh mục thành công");
    }

    @PutMapping("/{categoryId}")
    public ApiResponse<?> updateCategory(
            @PathVariable Long categoryId,
            @Valid @RequestBody CategoryUpdateRequest req
    ) {
        return ApiResponse.success(categoryService.updateCategory(categoryId, req), "Cập nhật danh mục thành công");
    }

    @DeleteMapping("/{categoryId}")
    public ApiResponse<?> deleteCategory(@PathVariable Long categoryId) {
        categoryService.removeCategory(categoryId);
        return ApiResponse.success(null, "xoá thành công");
    }

    @GetMapping("/{categoryId}")
    public ApiResponse<?> getCategoryById(@PathVariable Long categoryId) {
        return ApiResponse.success(categoryService.getCategoryById(categoryId), "Lấy thành công danh mục");
    }

    @GetMapping
    public ApiResponse<?> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<?> result = categoryService.getAllCategories(pageable);
        return ApiResponse.success(result, "Lấy thành công danh sách danh mục");
    }

}
