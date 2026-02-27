/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.web.repository;

import com.web.entity.TopupIntentEntity;
import com.web.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author ZZ
 */
public interface TopupIntentRepository extends JpaRepository<TopupIntentEntity,Long> {

    TopupIntentEntity findByIdAndStatus(Long topupId,PaymentStatus paymentStatus);
    
}
