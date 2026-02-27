package com.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
@Data
@Setter
@Getter
public class UserDTO {
    private Long id;

    @NotBlank(message = "fullName must be not null")
    private String fullName;

    @Email
    private String email;

    @NotBlank(message = "username must be not null")
    private String username;

    @NotBlank(message = "password must be not null")
    private String password;

    @NotBlank(message = "retype password must be not null")
    private String retype_password;


    @NotBlank(message = "number phone must be not null")
    @Pattern(
            regexp = "^(\\+84|0[3|5|7|9][0-9]{8}$)",
            message = "Invalid phone not format"
    )
    private String phoneNumber;

    private String address;

    private boolean isActive;

    private int totalVnd;

    private int vnd;

    private LocalDateTime created_at;
    private LocalDateTime updated_at;



}
