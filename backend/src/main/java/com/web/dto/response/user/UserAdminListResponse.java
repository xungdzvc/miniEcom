package com.web.dto.response.user;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class UserAdminListResponse {
    private Long id;
    private String fullName;
    private String email;
    private String username;
    private int totalVnd;
    private int vnd;
    private String address;
    private String phoneNumber;
    private LocalDateTime createdAt;
    private boolean status;
    private String role;
}
