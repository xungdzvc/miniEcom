/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.web.repository;

import com.web.entity.SystemBankAccountEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author ZZ
 */
public interface SystemBankAccountRepository extends JpaRepository<SystemBankAccountEntity, Long> {
  SystemBankAccountEntity findFristByIsDefaultTrue();
}
