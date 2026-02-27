package com.web.entity;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "coupon_code")
@Data
public class CouponEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "coupon_code")
    private String couponCode;

    @Column(name = "discount")
    private int discount;
}
