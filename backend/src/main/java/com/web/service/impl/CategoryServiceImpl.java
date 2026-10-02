package com.web.service.impl;

import com.web.dto.request.category.CategoryCreateRequest;
import com.web.dto.request.category.CategoryUpdateRequest;
import com.web.dto.response.category.AdminCategoryResponse;
import com.web.dto.response.category.CategoryResponse;
import com.web.entity.CategoryEntity;
import com.web.exception.MyException;
import com.web.mapper.CategoryMapper;
import com.web.repository.CategoryRepository;
import com.web.repository.ProductRepository;
import com.web.service.ICategoryService;
import com.web.util.StringNormalizer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements ICategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    private final CategoryMapper mapper;

    @Override
    public CategoryResponse createCategory(CategoryCreateRequest req) {
        String nameCategory = StringNormalizer.normalizeName(req.getName());
        if (req.getName().isBlank()) {
            throw new MyException("Tên danh mục không được để trống");
        }

        if(categoryRepository.existsByName(req.getName())){
            throw new MyException("Tên danh mục này đã tồn tại trong hệ thống");
        }
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setName(nameCategory);
        categoryEntity.setStatus(true);
        if(req.getParentId() != null){
            CategoryEntity parentCategory = categoryRepository.findById(req.getParentId()).orElseThrow(()-> new MyException("Danh mục cha không tồn tại"));
            if(parentCategory.getParentCategory() != null){
                throw new MyException("Danh mục chỉ được phép tồn tại tối đa 2 cấp");
            }
            if(categoryRepository.existsByNameAndParentCategory_Id(nameCategory, req.getParentId())){
                throw new MyException("Tên danh mục con này đã tồn tại trong danh mục cha");
            }
            categoryEntity.setParentCategory(parentCategory);
        }
        categoryRepository.save(categoryEntity);
        return mapper.toDTO(categoryEntity);
    }

    @Override
    public void removeCategory(Long id) {
        CategoryEntity categoryEntity = categoryRepository.findById(id).orElseThrow(() -> new MyException("Danh mục không tồn tại"));
        if(productRepository.countByCategoryId(categoryEntity.getId()) > 0){
            throw new MyException("Không thể xoá danh mục đang có sản phẩm");
        }
        categoryRepository.delete(categoryEntity);
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryUpdateRequest req) {
        String nameCategory = StringNormalizer.normalizeName(req.getName());
        CategoryEntity categoryEntity = categoryRepository.findById(id).orElseThrow(() -> new MyException("Danh mục không tồn tại"));
        if(categoryRepository.existsByNameAndIdIsNot(nameCategory,id)){
            throw new MyException("Tên Danh mục mới trùng khớp với danh mục hiện có trong database");
        }
        categoryEntity.setName(nameCategory);
        if(req.getParentId() != null){
            if(req.getParentId().equals(id)){
                throw new MyException("Bạn không được làm cha chính mình");
            }
            CategoryEntity parentCategory = categoryRepository.findById(req.getParentId()).orElseThrow(()-> new MyException("Danh mục cha không tồn tại"));
            if(parentCategory.getParentCategory() != null){
                throw new MyException("Danh mục chỉ được phép tồn tại tối đa 2 cấp");
            }
            if(categoryRepository.existsByNameAndParentCategory_Id(nameCategory, req.getParentId())){
                throw new MyException("Tên danh mục con này đã tồn tại trong danh mục cha");
            }
            categoryEntity.setParentCategory(parentCategory);
        }

        categoryRepository.save(categoryEntity);
        return mapper.toDTO(categoryEntity);

    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        CategoryEntity categoryEntity = categoryRepository.findById(id).orElseThrow(() -> new MyException("Danh mục không tồn tại"));
        return mapper.toDTO(categoryEntity);
    }

    @Override
    public Page<AdminCategoryResponse> getAllCategories(Pageable pageable) {
        Page<CategoryEntity> page = categoryRepository.findAll(pageable);
        return page.map(entity -> {
            AdminCategoryResponse dto = mapper.toAdminDTO(entity);
            dto.setQuantity(productRepository.countByCategoryId(entity.getId()));
            return dto;
        });
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        List<CategoryEntity> categories = categoryRepository.findAllByStatus(true);
        return categories.stream().map(mapper::toDTO).toList();
    }

    @Override
    public int getCount() {
        return (int) categoryRepository.count();
    }

   
}
