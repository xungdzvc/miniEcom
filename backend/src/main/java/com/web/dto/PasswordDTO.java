package com.web.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PasswordDTO {
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;

}
