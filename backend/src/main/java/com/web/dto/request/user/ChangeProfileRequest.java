package com.web.dto.request.user;

import lombok.Data;

@Data
public class ChangeProfileRequest {
    private String fullName;
    private String phoneNumber;
}
