/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.entity;

import com.web.enums.MatchType;
import com.web.enums.PaymentStatus;
import com.web.enums.PaymentType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Data;

/**
 *
 * @author ZZ
 */
@Data
@Entity
@Table(name = "payment_transactions")
public class PaymentTransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type")
    private PaymentType paymentType;
    
    @Column(name = "payment_name")
    private String paymentName;
    
    @Column(name = "payment_ref")
    private String paymentRef;
    
    @Column(name = "amount")
    private Long amount;
    
    @Column(name = "currency")
    private String currency;
    
    @Column(name = "transaction_content")
    private String transactionContent;
    
    @Column(name = "card_type")
    private String cardType;
    
    @Column(name = "card_code")
    private String cardCode;
    
    @Column(name = "card_serial")
    private String cardSerial;
    
    @Column(name = "bank_account")
    private String bankAccount;

    @Enumerated(EnumType.STRING)
    @Column(name = "pay_status")
    private PaymentStatus paymentStatus;  
    
    @Enumerated(EnumType.STRING)
    @Column(name = "match_type")
    private MatchType matchType; 
    
    
    @Column(name = "match_ref")
    private String matchRef;
    
    @Column(name = "order_id")
    private Long orderId;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    
    
}
