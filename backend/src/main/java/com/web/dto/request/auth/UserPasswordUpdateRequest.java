/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.dto.request.auth;

import lombok.Data;

/**
 *
 * @author ZZ
 */
@Data
public class UserPasswordUpdateRequest {
    private String oldPassword;
    private String newPassword;
    private String retypePassword;
}
