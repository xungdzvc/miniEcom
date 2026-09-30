package com.web.repository;
 
import com.web.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
 
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<ProductEntity,Long> {

    @EntityGraph(attributePaths = {
        "category",
        "productDetail",
        
    })
    @Override
    Page<ProductEntity> findAll(Pageable pageale);
           
    @EntityGraph(attributePaths = {
        "category",
        "productDetail"
        
    })
    Page<ProductEntity> findByStatus(Boolean status,Pageable pageable);

    int countByCategoryId(Long categoryId);

    @EntityGraph(attributePaths = {
        "category",
        "productDetail",
        "user"
        
    })
    Page<ProductEntity> findByUser_Id(Long userId,Pageable pageable);
    
    ProductEntity findBySlug(String slug);

    @Query("select count(p) from ProductEntity p where p.status = :status")
    int countByStatus(@Param("status") Boolean status);

    int countByCategory_Id(Long categoryId);

    @Query("select p from ProductEntity p where p.id = :id and p.status = :status")
    ProductEntity findByIdAndStatus(@Param("id") Long id, @Param("status") Boolean status);

    Page<ProductEntity> findByCategoryId(Long categoryId,Pageable pageable);
    boolean existsBySlug(String slug);


    
}
