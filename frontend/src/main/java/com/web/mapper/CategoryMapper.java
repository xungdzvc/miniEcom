package com.web.mapper;
 
import com.web.dto.request.category.CategoryCreateRequest;
import com.web.dto.request.category.CategoryUpdateRequest;
import com.web.dto.response.category.CategoryResponse;
import com.web.dto.response.category.AdminCategoryResponse;
import com.web.entity.CategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target="parentId", source="parentCategory.id")
    @Mapping(target="parentName", source="parentCategory.name")
    CategoryResponse toDTO(CategoryEntity categoryEntity);
    
    @Mapping(target="quantity", ignore = true)
    @Mapping(target="parentId", source="parentCategory.id")
    @Mapping(target="parentName", source="parentCategory.name")
    AdminCategoryResponse toAdminDTO(CategoryEntity categoryEntity);

}
