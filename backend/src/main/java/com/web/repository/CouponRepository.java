package com.web.repository;

import com.web.entity.CartItemEntity;
import com.web.entity.CouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<CouponEntity, Long> {
    @Query("select c.discount from CouponEntity c where c.couponCode  = :couponCode ")
    Optional<Integer> findDiscountPercentByCouponCode(@Param("couponCode") String id);
    
    
    
}
