package com.web.mapper;

import com.web.dto.CategoryDTO;
import com.web.entity.CategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "quantity", ignore = true)
    CategoryDTO toDTO(CategoryEntity categoryEntity);
    
    CategoryEntity toEntity(CategoryDTO categoryDTO);
}
