package com.web.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "carts")
@Getter
@Setter
public class CartEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;


    @OneToMany(mappedBy = "cart",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<CartItemEntity> cartItems = new ArrayList<>();

    public List<ProductEntity> getProducts() {
        if (cartItems == null) return new ArrayList<>();
        return cartItems.stream()
                .map(CartItemEntity::getProduct)
                .collect(Collectors.toList());
    }

    public Long getId(){
        return this.id;
    }
}
