package com.web.service;

import com.web.dto.CartDTO;
import com.web.dto.CategoryDTO;

import java.util.List;

public interface ICategoryService {
    CategoryDTO addCategory(CategoryDTO categoryDTO);
    CategoryDTO removeCategory(Long id);
    CategoryDTO updateCategory(Long id,CategoryDTO categoryDTO);
    CategoryDTO getCategoryById(Long id);
    List<CategoryDTO> getAllCategories();
    int getCount();
}
