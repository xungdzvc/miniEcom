package com.web.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "categories")

@Getter
@Setter
public class CategoryEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonBackReference
    private CategoryEntity parentCategory;

    @Column(name = "name",nullable = false,unique = true)
    private String name;

    @Column(name = "status")
    private Boolean status ;

    @OneToMany(mappedBy = "parentCategory")
    @JsonManagedReference
    private List<CategoryEntity> childrenCategory = new ArrayList<>();




}
