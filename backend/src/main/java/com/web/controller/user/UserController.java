package com.web.controller.user;

import com.web.dto.PasswordDTO;
import com.web.dto.UserDTO;
import com.web.dto.request.user.ChangeProfileRequest;
import com.web.dto.request.user.UserUpdateRequest;
import com.web.dto.response.common.ApiResponse;
import com.web.security.SecurityUtil;
import com.web.service.IPaymentTransactionService;
import com.web.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    IUserService userService;
    private final IPaymentTransactionService iPaymentTransactionService;

    @PutMapping("/password-change")
    public ResponseEntity<?> changePassword( @RequestBody PasswordDTO passworDTO) {
        Long id = SecurityUtil.getUserId();
        return ResponseEntity.ok(userService.updatePassword(id, passworDTO));
    }

    @PutMapping("/profile-edit")
    public ApiResponse<?> updateProfile(@Valid @RequestBody UserUpdateRequest changeProfileRequest) {
        Long id = SecurityUtil.getUserId();
        return ApiResponse.success(userService.updateProfile(id, changeProfileRequest));
    }

    
    
    @GetMapping("/topup-history")
    public ApiResponse<?> getTopups() {
        Long id = SecurityUtil.getUserId();
        return ApiResponse.success(iPaymentTransactionService.getTopup(id));
    }
    
}
