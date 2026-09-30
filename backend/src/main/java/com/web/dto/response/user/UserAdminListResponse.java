package com.web.dto.response.user;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class UserAdminListResponse {
    private Long id;
    private String fullName;
    private String email;
    private String username;
    private BigDecimal totalDeposit;
    private BigDecimal currentBalance;
    private String address;
    private String phoneNumber;
    private Instant createdAt;
    private Boolean status;
    private String role;
}
