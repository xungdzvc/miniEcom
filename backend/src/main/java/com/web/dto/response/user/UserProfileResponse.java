/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.dto.response.user;

import lombok.Data;

/**
 *
 * @author ZZ
 */
@Data
public class UserProfileResponse {
    String fullName;
    String email;
    String username;
    String phoneNumber;
    String address;
    String googleId;
    float totalVnd;
    float vnd;
    
}
