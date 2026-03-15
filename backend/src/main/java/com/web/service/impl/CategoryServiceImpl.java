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
            throw new MyException("Tên danh mục không được để trống");
        }
        LocalDateTime now = LocalDateTime.now();
        categoryDTO.setCreatedAt(now);
        categoryDTO.setUpdatedAt(now);
        CategoryEntity categoryEntity = mapper.toEntity(categoryDTO);
        categoryRepository.save(categoryEntity);
        return mapper.toDTO(categoryEntity);
    }

    @Override
    public CategoryDTO removeCategory(Long id) {
        CategoryEntity categoryEntity = categoryRepository.findById(id).orElseThrow(() -> new MyException("Danh mục không tồn tại"));

        categoryRepository.delete(categoryEntity);
        return mapper.toDTO(categoryEntity);

    }

    @Override
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        CategoryEntity categoryEntity = categoryRepository.findById(id).orElseThrow(() -> new MyException("Danh mục không tồn tại"));
        categoryEntity.setName(categoryDTO.getName());
        categoryEntity.setUpdatedAt(LocalDateTime.now());
        categoryRepository.save(categoryEntity);
        return mapper.toDTO(categoryEntity);

    }

    @Override
    public CategoryDTO getCategoryById(Long id) {
        CategoryEntity categoryEntity = categoryRepository.findById(id).orElseThrow(() -> new MyException("Danh mục không tồn tại"));
        {
            return mapper.toDTO(categoryEntity);
        }
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        List<CategoryEntity> categories = categoryRepository.findAll();
        List<CategoryDTO> categoriesDTO = new ArrayList<>();

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
