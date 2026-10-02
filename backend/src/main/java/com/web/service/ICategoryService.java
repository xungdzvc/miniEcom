package com.web.service;

import com.web.dto.request.category.CategoryCreateRequest;
import com.web.dto.request.category.CategoryUpdateRequest;
import com.web.dto.response.category.AdminCategoryResponse;
import com.web.dto.response.category.CategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ICategoryService {
    CategoryResponse createCategory(CategoryCreateRequest req);
    void removeCategory(Long id);
    CategoryResponse updateCategory(Long id, CategoryUpdateRequest req);
    CategoryResponse getCategoryById(Long id);
    Page<AdminCategoryResponse> getAllCategories(Pageable pageable);
    List<CategoryResponse> getAllCategories();
    int getCount();
}
