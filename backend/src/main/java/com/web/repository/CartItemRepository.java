package com.web.repository;

import com.web.entity.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {
    CartItemEntity findByCartIdAndProductId(Long cartId, Long productId);
    List<CartItemEntity> findByCartId(Long userId);
    @Modifying
    @Query("""
           update CartItemEntity ci
           set ci.quantity = :quantity
           where ci.id = :cartItemId
           and ci.cart.user.id = :userId
           """)

    int updateQty(@Param("userId") Long userId,
            @Param("cartItemId") Long cartItemId,
            @Param("quantity") Integer quantity);
}
