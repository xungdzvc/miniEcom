package com.web.entity;

import com.web.enums.PaymentMethod;
import com.web.enums.PaymentStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
 

@Entity
@Table(name = "order_payments")
@Getter
@Setter
public class OrderPaymentEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private PaymentTransactionEntity transaction;

    @Column(name = "transfer_content")
    private String transferContent;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "payment_method")
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
 
}
