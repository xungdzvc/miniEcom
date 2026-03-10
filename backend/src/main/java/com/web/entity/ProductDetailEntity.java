package com.web.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "product_detail")
public class ProductDetailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "quantity")
    private Integer quantity;
    
    @Column(name = "download_url")
    private String  downloadUrl;

    @Column(name = "youtube_url")
    private String  youtubeUrl;

    @Column(name = "demo_url")
    private String  demoUrl;

    @Column(name = "discount")
    private Integer discount;
    
    @Column(name = "view_count")
    private Integer viewCount;

    @Column(name = "sales_count")
    private Integer saleCount;

    @Column(name = "technology")
    private String technology;

    @Column(name = "install_tutorial")
    private String installTutorial;

    @Column(name = "pin")
    private Boolean pin;
    
    @Column(name = "share_by")
    private String shareBy;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="product_id",nullable = false)
    private ProductEntity product;
    
    
}
