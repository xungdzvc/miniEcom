/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.web.repository;

import com.web.entity.TopupIntentEntity;
import com.web.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

/**
 *
 * @author ZZ
 */
public interface TopupIntentRepository extends JpaRepository<TopupIntentEntity,Long> {

    Optional<TopupIntentEntity> findByIdAndUserId(Long id,Long userId);

    @Query("""
            select t from TopupIntentEntity t 
            where t.id = :topupId and t.status = :paymentStatus
            and t.expiredAt > :now
            """)
    Optional<TopupIntentEntity> findByIdAndStatusAndNotExpiredAt(@Param("topupId") Long topupId, @Param("paymentStatus") PaymentStatus paymentStatus,@Param("now") Instant now);
    
}
