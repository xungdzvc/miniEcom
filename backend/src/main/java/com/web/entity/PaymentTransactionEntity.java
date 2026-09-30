package com.web.entity;

import com.web.enums.MatchType;
import com.web.enums.PaymentStatus;
import com.web.enums.PaymentType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
 
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "payment_transactions")
public class PaymentTransactionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false)
    private PaymentType paymentType;

    @Column(name = "payment_name")
    private String paymentName;

    @Column(name = "payment_ref", unique = true)
    private String paymentRef;

    @Column(name = "amount")
    private BigDecimal amount;

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
    
    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderPaymentEntity> orderPayments = new ArrayList<>();

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TopupPaymentEntity> topupPayments = new ArrayList<>();

    
}
