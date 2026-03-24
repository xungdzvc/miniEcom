package com.web.entity;


import jakarta.persistence.*;
import java.time.LocalDateTime; 
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "coupon_code")
@Getter
@Setter
public class CouponEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "coupon_code")
    private String couponCode;

    @Column(name = "discount")
    private int discount;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
