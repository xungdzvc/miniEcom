/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.repository;

import com.web.entity.ReviewEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author ZZ
 */
public interface ReviewRepository extends JpaRepository<ReviewEntity,Long>{
    List<ReviewEntity> findByProductId(Long productId);
}
