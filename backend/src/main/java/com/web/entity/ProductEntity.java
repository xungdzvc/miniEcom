package com.web.entity;
 
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList; 
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Entity
@Table(name = "products",
        indexes = {
            @Index(name = "idx_product_status",columnList = "status"),
            @Index(name = "idx_product_category_id",columnList = "category_id"),
            @Index(name = "idx_product_slug",columnList = "slug")
        })
@Getter
@Setter
public class ProductEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "thumbnail")
    private String thumbnail;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "status")
    private Boolean status;

    @Column(unique = true, nullable = false)
    private String slug;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true,fetch =  FetchType.LAZY)
    private ProductDetailEntity productDetail;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImageEntity> productImages = new ArrayList<>();
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewEntity> reviews = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;
 

}
