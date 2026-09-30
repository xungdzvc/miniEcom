package com.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PasswordDTO {
    @NotNull(message =  "Mật khẩu cũ không thể để trống")
    private String oldPassword;
    @NotNull(message =  "Mật khẩu mới không thể để trống")
    private String newPassword;
    @NotNull(message =  "Mật khẩu nhập lại không thể để trống")
    private String confirmPassword;

}
