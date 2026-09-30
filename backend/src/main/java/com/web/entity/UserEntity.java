package com.web.entity;

import com.web.enums.Provider; 
import com.web.enums.Role;
import jakarta.persistence.*; 
import java.math.BigDecimal; 
import java.util.*;
import lombok.Getter;
import lombok.Setter; 

@Entity
@Table(name="users")
@Getter
public class UserEntity extends BaseEntity{
   
    @Setter
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;
    
    @Setter
    @Column(name = "fullname")
    private String fullName;

    @Setter
    @Column(name = "email",unique = true,nullable = false) 
    private String email;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "auth_provider")
    private Provider provider;

    @Setter
    @Column(name = "google_id")
    private String googleId;

    @Setter
    @Column(name = "username")
    private String username;

    @Setter
    @Column(name = "password")
    private String password;

    @Setter
    @Column(name = "address")
    private String address;

    @Setter
    @Column(name = "phonenumber")
    private String phoneNumber;

    @Setter
    @Column(name = "is_active")
    private Boolean isActive;
    
    @Column(name = "lifetime_deposit", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalDeposit;

    @Column(name = "current_balance", nullable = false, precision = 19, scale = 4)
    private BigDecimal currentBalance;

    @Setter
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name="user_roles",
            joinColumns = @JoinColumn(name ="user_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name ="role_id",nullable = false))
    private List<RoleEntity> roles = new ArrayList<>();

    @Setter
    @OneToMany(mappedBy = "user" )
    private List<OrderEntity> orders = new ArrayList<>();

    @Setter
    @OneToOne(mappedBy = "user" ,orphanRemoval = true, fetch = FetchType.LAZY)
    private CartEntity cart;

    @Setter
    @OneToMany(mappedBy = "user")
    private Set<BankAccountEntity> bankAccounts = new HashSet<>();

    public void deposit(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Giá trị không được để trống");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException();
        }
        this.totalDeposit = totalDeposit.add(amount);
        this.currentBalance = currentBalance.add(amount);
    }

    public void wallet(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Giá trị không được để trống");
        }
        if(amount.compareTo(BigDecimal.ZERO) == 0){
            return;
        }
        if(amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException();
        }
        if(this.currentBalance.compareTo(amount) < 0){
            throw new IllegalArgumentException("Số tiền của bạn không đủ để thanh toán");
        }
        this.currentBalance = this.currentBalance.subtract(amount);
    }

    @PrePersist
    public void prePersist(){
        this.initDefaultUser();
    }
    public void initDefaultUser(){
        this.totalDeposit = BigDecimal.ZERO;
        this.currentBalance = BigDecimal.ZERO;
        this.isActive = true;
        this.address = "";
        this.phoneNumber = "";   
    }

    
    
    
    
    
}
