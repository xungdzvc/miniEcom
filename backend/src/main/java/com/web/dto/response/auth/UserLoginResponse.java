/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.dto.response.auth;

import lombok.Data;

/**
 *
 * @author ZZ
 */

@Data
public class UserLoginResponse {
    private String accessToken;
    private String refreshToken;
    private UserDTOResponse user;
    
}
