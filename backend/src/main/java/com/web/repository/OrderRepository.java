package com.web.repository;

import com.web.entity.OrderEntity;
import com.web.enums.OrderStatus;
import com.web.enums.PaymentStatus;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    OrderEntity findByUserId(Long id);

    OrderEntity getStatusById(Long id);

    List<OrderEntity> findByUserIdAndStatus(Long id, OrderStatus status);
    
    OrderEntity findByIdAndStatus(Long id, OrderStatus status );

    @Query("""
           select o from OrderEntity o
           where o.user.id = :userId and ( o.status = :status
           or o.expiresAt >= :now )
           """)
    List<OrderEntity> findSuccessOrNotExpired(
            @Param("userId")Long userId,@Param("status") OrderStatus status,@Param("now") LocalDateTime now
    );

    @Transactional
    @Modifying
    @Query("""
           update OrderEntity o
           set o.status = com.web.enums.OrderStatus.EXPIRED
           where o.status = com.web.enums.OrderStatus.PENDING 
           and o.expiresAt <= :now
           """)
    void markExpired(@Param("now")LocalDateTime now);
    
    
    @Query("""
           select coalesce(SUM(o.total), 0)
               FROM OrderEntity o
           where o.status = com.web.enums.OrderStatus.SUCCESS
           and o.orderDate >= :start
           and o.orderDate < :end
           """)
    Long sumRevenueBetween(
            @Param("start")LocalDateTime start,
            @Param("end")LocalDateTime end);
    
}
