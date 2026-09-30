package com.web.repository;

import com.web.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.yaml.snakeyaml.tokens.Token;

import java.util.List;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    boolean existsByName(String name);
    boolean existsByNameAndIdIsNot(String name,Long categoryId);
    boolean existsByNameAndParentCategory_Id(String name,Long categoryId);
    List<CategoryEntity> findAllByStatus(Boolean status);
}
