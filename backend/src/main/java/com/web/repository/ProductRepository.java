package com.web.repository;

import com.web.dto.ProductDTO;
import com.web.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductEntity,Long> {
    List<ProductEntity> findByStatusTrue();
    List<ProductEntity> findByStatusFalse();
    int countByCategoryId(Long categoryId);
    List<ProductEntity> findByUser_Id(Long userId);
    ProductEntity findBySlug(String slug);
    int countByStatusTrue();
    int countByStatusFalse();
    ProductEntity findByIdAndStatusTrue(Long id);
    public List<ProductEntity> findByCategoryId(Long categoryId);
    
}
