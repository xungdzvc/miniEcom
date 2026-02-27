/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author ZZ
 */
@Entity
@Table(name = "reviews")
@Setter
@Getter
public class ReviewEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name  = "product_id", nullable = false)
    private ProductEntity product;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="user_id",nullable = false)
    private UserEntity user;
    
    @Column(name = "rating")
    private int rate;
    
    @Column(name = "comment")
    private String comment;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
}
