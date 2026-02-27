/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.web.repository;

import com.web.entity.BankAccountEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author ZZ
 */
public interface BankAccountRepository extends JpaRepository<BankAccountEntity, Long> {
  List<BankAccountEntity> findByUserIdAndActiveTrue(Long userId);
}