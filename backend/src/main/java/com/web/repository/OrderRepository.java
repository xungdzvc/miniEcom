package com.web.repository;

import com.web.entity.OrderEntity;
import com.web.enums.OrderStatus; 
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> { 
    OrderEntity getStatusById(Long id);

    Optional<OrderEntity> findByUserIdAndStatus(Long id, OrderStatus status);

    Optional<OrderEntity> findByIdAndStatus(Long id, OrderStatus status );

    Optional<OrderEntity> findByIdAndUserId(Long id,Long userId );

// @Query("SELECT o FROM OrderEntity o WHERE o.user.id = :userId AND o.status = :status")
//     Page<OrderEntity> findByUserAndStatus(@Param("userId") Long userId, 
//                                            @Param("status") OrderStatus status,
//                                            Pageable pageable);
    @Query("""
           select o from OrderEntity o
           where o.user.id = :userId and ( o.status = :status
           or o.expiresAt >= :now )
           """)
    List<OrderEntity> findSuccessAndNotExpired(
            @Param("userId")Long userId,@Param("status") OrderStatus status,@Param("now") Instant now
    );

    @Transactional
    @Modifying
    @Query("""
           update OrderEntity o
           set o.status = com.web.enums.OrderStatus.EXPIRED
           where o.status = com.web.enums.OrderStatus.PENDING 
           and o.expiresAt <= :now
           """)
    void markExpired(@Param("now")Instant now);

    @Modifying
    @Query("""
            update OrderEntity o 
            set o.status = com.web.enums.OrderStatus.EXPIRED
            where o.status = com.web.enums.OrderStatus.PENDING 
            and o.id = :orderId
            """)
    int makeExpiredByOrderId(@Param("orderId") Long orderId);

    @Query("""
            select o.id
            from OrderEntity o
            where o.status = com.web.enums.OrderStatus.PENDING
            and o.expiresAt <= :now
            """)
    List<Long> findExpiredPendingOrderIds(@Param("now") Instant now);


    @Query("""
           select coalesce(SUM(o.total), 0)
               FROM OrderEntity o
           where o.status = com.web.enums.OrderStatus.SUCCESS
           and o.orderDate >= :start
           and o.orderDate < :end
           """)
    BigDecimal sumRevenueBetween(
            @Param("start")Instant start,
            @Param("end")Instant end);


    
    @Query("select count(oi) > 0 from OrderItemEntity oi "
            + "where oi.order.user.id = :userId "
            + "and oi.product.id = :productId "
            + "and oi.order.status = 'SUCCESS'")
    boolean existsPurchaseByUserAndProduct(@Param("userId") Long userId, @Param("productId") Long productId);
}
