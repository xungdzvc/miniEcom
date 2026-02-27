package com.web.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter

public class UserLoginRequest {

    @NotBlank(message = "Username must be not empty")
    private String username;

    @NotBlank(message = "Password must be not empty")
    private String password;
}
