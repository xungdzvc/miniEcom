/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.dto.response.auth;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author ZZ
 */
@RequiredArgsConstructor
@Data
public class UserDTOResponse {
    private Long id;
    private String fullName;
    private String email;
    private String username;
    private String phoneNumber;
    private String address;
    private boolean active;
    private int totalVnd;
    private int vnd;
    private String roles;
    private String googleId;
}
