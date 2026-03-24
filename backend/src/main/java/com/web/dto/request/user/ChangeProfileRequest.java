package com.web.dto.request.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeProfileRequest {
    private String fullName;
    private String phoneNumber;
}
