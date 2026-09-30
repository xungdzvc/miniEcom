/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.entity;

import com.web.enums.PaymentStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;

import java.time.Instant;
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
public class TopupIntentEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id",nullable = false)
    private UserEntity user;

    @Column(name = "payment_status")
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "expired_at")
    private Instant expiredAt;
     

}
