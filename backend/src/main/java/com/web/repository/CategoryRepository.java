package com.web.repository;

import com.web.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.yaml.snakeyaml.tokens.Token;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
}
