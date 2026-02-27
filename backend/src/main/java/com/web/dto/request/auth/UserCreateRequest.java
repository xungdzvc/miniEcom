/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 *
 * @author ZZ
 */
@Data
public class UserCreateRequest {
    
    @Email
    private String email;

    @NotBlank(message = "username must be not null")
    private String username;

    @NotBlank(message = "password must be not null")
    private String password;

    @NotBlank(message = "retype password must be not null")
    private String retype_password;

//    @NotBlank(message = "number phone must be not null")
//    @Pattern(
//            regexp = "^(\\+84|0[3|5|7|9][0-9]{8}$)",
//            message = "Invalid phone not format"
//    )
    private String phoneNumber;
}
