/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 *
 * @author ZZ
 */
@Data
public class UserUpdateRequest {
    private String fullName;
    @NotBlank(message = "Số điện thoại không thể để trống")
    @Pattern(
            regexp = "^(\\+84|0[3|5|7|9][0-9]{8}$)",
            message = "Số điện thoại không đúng định dạng"
    )
    private String phoneNumber;
}
