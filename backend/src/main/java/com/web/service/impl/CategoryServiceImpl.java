package com.web.service.impl;

import com.web.dto.CategoryDTO;
import com.web.entity.CategoryEntity;
import com.web.exception.MyException;
import com.web.mapper.CategoryMapper;
import com.web.repository.CategoryRepository;
import com.web.repository.ProductRepository;
import com.web.service.ICategoryService;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements ICategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    private final CategoryMapper mapper;

    @Override
    public CategoryDTO addCategory(CategoryDTO categoryDTO) {
        if (categoryDTO.getName().equals("")) {
            throw new MyException("category name is not empty");
        }
        LocalDateTime now = LocalDateTime.now();
        categoryDTO.setCreatedAt(now);
        categoryDTO.setCreatedAt(now);
        CategoryEntity categoryEntity = mapper.toEntity(categoryDTO);
        categoryRepository.save(categoryEntity);
        return mapper.toDTO(categoryEntity);
    }

    @Override
    public CategoryDTO removeCategory(Long id) {
        CategoryEntity categoryEntity = categoryRepository.findById(id).orElseThrow(() -> new MyException("Category not found"));

        if (categoryEntity != null) {
            categoryRepository.delete(categoryEntity);
            return mapper.toDTO(categoryEntity);
        } else {
            throw new MyException("Category not exists");
        }
    }

    @Override
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        CategoryEntity categoryEntity = categoryRepository.findById(id).orElseThrow(() -> new MyException("Category not found"));
        if (categoryEntity != null) {
            categoryEntity.setName(categoryDTO.getName());
            categoryEntity.setUpdatedAt(LocalDateTime.now());
            categoryRepository.save(categoryEntity);
            return mapper.toDTO(categoryEntity);
        } else {
            throw new MyException("Category not exists");
        }

    }

    @Override
    public CategoryDTO getCategoryById(Long id) {
        CategoryEntity categoryEntity = categoryRepository.findById(id).orElseThrow(() -> new MyException("Category not found"));
        if (categoryEntity == null) {
            throw new MyException("Category not exists");
        } else {
            return mapper.toDTO(categoryEntity);
        }
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        List<CategoryEntity> categories = categoryRepository.findAll();
        List<CategoryDTO> categoriesDTO = new ArrayList<CategoryDTO>();

        for (CategoryEntity categoryEntity : categories) {
            CategoryDTO categoryDTO = mapper.toDTO(categoryEntity);
            int quantities = productRepository.countByCategoryId(categoryEntity.getId());
            categoryDTO.setQuantity(quantities);
            categoriesDTO.add(categoryDTO);

        }
        return categoriesDTO;
    }

    @Override
    public int getCount() {
        return (int) categoryRepository.count();
    }
}
