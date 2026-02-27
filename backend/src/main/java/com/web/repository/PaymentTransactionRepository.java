/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.web.repository;

import com.web.entity.PaymentTransactionEntity;
import com.web.enums.PaymentStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author ZZ
 */
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransactionEntity, Long> {
    boolean existsByPaymentRef(String paymentRef);
    PaymentTransactionEntity findByPaymentRef(String paymentRef);
    List<PaymentTransactionEntity> findAllByUserId(Long userId);
    PaymentTransactionEntity findByPaymentStatusAndCardCodeAndCardSerial(PaymentStatus paymentStatus,
            String cardCode, String cardSerial);
    
}
