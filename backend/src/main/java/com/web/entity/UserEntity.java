package com.web.entity;

import com.web.enums.Provider;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.*;
import org.checkerframework.common.aliasing.qual.Unique;

@Entity
@Table(name="users")
@Data
public class UserEntity {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fullname")
    private String fullName;

    @Column(name = "email")
    @Unique
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_provider")
    private Provider provider;
    
    @Column(name = "google_id")
    private String googleId;
    
    @Unique
    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "address")
    private String address;

    @Column(name = "phonenumber")
    private String phoneNumber;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_active")
    private Boolean isActive;
    
    @Column(name = "total_vnd")
    private Long totalVnd;

    @Column(name = "vnd")
    private Long vnd;



    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="user_roles",
            joinColumns = @JoinColumn(name ="user_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name ="role_id",nullable = false))
    private List<RoleEntity> roles = new ArrayList<>();


    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<OrderEntity> orders = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL ,orphanRemoval = true, fetch = FetchType.LAZY)
    private CartEntity cart;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<BankAccountEntity> bankAccounts = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void subVnd(Long num){
        this.vnd -= num;
    }
    public void addBlance(Long num){
        this.vnd += num;
        this.totalVnd += num;
    }


}
