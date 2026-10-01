package com.web.dto.request.auth;

import jakarta.validation.ValidationException;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterRequest {

    @Email
    String email;

    @NotBlank(message = "Tài khoản không được để trống")
    @Size(min = 4, max = 50, message = "Tài khoản phải từ 4 đến 50 ký tự")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Tài khoản chỉ được chứa chữ cái, số và dấu gạch dưới")
    String username;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
            message = "Mật khẩu phải có chữ hoa, chữ thường, số")
    String password;

    @NotBlank(message = "Mật khẩu xác nhận không được để trống")
    private String retypePassword;

    public void validate() {
        if (!password.equals(retypePassword)) {
            throw new ValidationException("Mật khẩu và mật khẩu xác nhận không chính xác");
        }
    }

}
