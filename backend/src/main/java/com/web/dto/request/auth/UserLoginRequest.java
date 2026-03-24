package com.web.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
 
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter

public class UserLoginRequest {

    @NotBlank(message = "Tài khoản không được để trống")
    private String username;

    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;
}
