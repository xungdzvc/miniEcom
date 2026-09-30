/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author ZZ
 */
@Getter
@Setter
public class UserAdminEditDTO {
    private String fullName;
    private String email;
    private String address;
    private String username;
    private String phoneNumber;
    private Boolean isActive;
    private Long currentBalance;
    private Long totalDeposit; 
    private List<Long> roleIds;
}
