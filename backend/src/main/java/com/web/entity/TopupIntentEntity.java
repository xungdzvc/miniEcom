/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.entity;

import com.web.enums.PaymentStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author ZZ
 */
@Getter
@Setter
@Entity
@Table(name = "topup_intent")
public class TopupIntentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    private long userId;
    
    @Enumerated(EnumType.STRING)
    @JoinColumn(name = "payment_status")
    private PaymentStatus status;

  
    
    private long amount;

    private LocalDateTime expiredAt;
    private LocalDateTime createdAt;
    
}
