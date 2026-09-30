package com.web.repository;

import com.web.entity.ProductDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDetailRepository extends JpaRepository<ProductDetailEntity,Long> {
    @Modifying
    @Query("""
            update ProductDetailEntity pd 
            set pd.quantity = pd.quantity - :quantity
            where pd.id = :productDetailEntityId and pd.quantity >= :quantity 
            """)
    int reserveStock(@Param("productDetailEntityId") Long productDetailEntityId, @Param("quantity") Integer quantity);

    @Modifying
    @Query("""
            update ProductDetailEntity pd 
            set pd.quantity = pd.quantity + :quantity
            where pd.id = :productDetailEntityId 
            """)
    void releaseStock(@Param("productDetailEntityId") Long productDetailEntityId, @Param("quantity") Integer quantity);

    @Modifying
    @Query("""
           update productDetailEntity pd 
           set pd.saleCount = pd.saleCount + :quantity
           where pd.id = :productDetailEntityId
           """ )
    void incrementSalesCount(@Param("productDetailEntityId") Long productDetailEntityId,@Param("quantity") int quantity);
}
