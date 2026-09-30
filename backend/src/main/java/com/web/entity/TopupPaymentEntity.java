package com.web.entity;

import com.web.enums.PaymentMethod;
import com.web.enums.PaymentStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
 

@Entity
@Table(name = "topup_payments")
@Getter
@Setter
public class TopupPaymentEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private PaymentTransactionEntity transaction;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "payment_method")
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(name = "card_type")
    private String cardType;

    @Column(name = "card_code")
    private String cardCode;

    @Column(name = "card_serial")
    private String cardSerial;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

     
}
