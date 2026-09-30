package com.web.repository;

import com.web.entity.OrderPaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderPaymentRepository extends JpaRepository<OrderPaymentEntity, Long> {
    List<OrderPaymentEntity> findByOrderId(Long orderId);
    Optional<OrderPaymentEntity> findByOrderIdAndStatus(Long orderId, com.web.enums.PaymentStatus status);
}
