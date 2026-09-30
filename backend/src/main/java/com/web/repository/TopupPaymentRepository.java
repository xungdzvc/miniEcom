package com.web.repository;

import com.web.entity.TopupPaymentEntity;
import com.web.enums.PaymentStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TopupPaymentRepository extends JpaRepository<TopupPaymentEntity, Long> {
    List<TopupPaymentEntity> findByUserId(Long userId);
    Optional<TopupPaymentEntity> findByTransactionId(Long transactionId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<TopupPaymentEntity> findByCardCodeAndCardSerialAndStatus(
            String cardCode, String cardSerial, PaymentStatus status);
    List<TopupPaymentEntity> findByUserIdOrderByCreatedAtDesc(Long userId);
}
