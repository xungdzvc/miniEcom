package com.web.controller.auth;

import com.web.dto.UserDTO;
import com.web.dto.request.auth.UserCreateRequest;
import com.web.dto.request.auth.UserGoogleLoginRequest;
import com.web.dto.request.auth.UserLoginRequest;
import com.web.dto.response.ResponseData;
import com.web.dto.response.auth.UserDTOResponse;
import com.web.security.JwtUtils;
import com.web.service.IUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.web.dto.response.auth.UserLoginResponse;
import com.web.dto.response.common.ApiResponse;
import com.web.entity.UserEntity;
import com.web.security.CustomUserDetails;
import com.web.security.SecurityUtil;
import com.web.service.google.GoogleAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IUserService userService;
    private final GoogleAuthService googleAuthService;
    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<UserDTOResponse> register(@RequestBody UserCreateRequest userCreateRequest) {
        UserDTOResponse userResponse = userService.register(userCreateRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userResponse);
    }

    @GetMapping("/me")
    public ApiResponse<?> getProfile() {
        Long id = SecurityUtil.getUserId();
        return ApiResponse.success(userService.getUserProfileById(id));
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@Valid @RequestBody UserLoginRequest userLoginRequest) {
        UserLoginResponse userLoginResponse = userService.login(userLoginRequest);

        ResponseCookie cookie = ResponseCookie.from("refresh_token", userLoginResponse.getRefreshToken())
                .httpOnly(true)
                .secure(false) // để false khi chạy localhost
                .sameSite("Lax")
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(userLoginResponse);
    }

    @PostMapping("/fresh-token")
    public ResponseEntity<?> refresh(
            @CookieValue("refresh_token") String refreshToken) {

        Long userId = jwtUtils.getUserId(refreshToken);
        UserEntity user = userService.getUserById(userId);

        CustomUserDetails userDetails = new CustomUserDetails(user);

        String newAccessToken = jwtUtils.generateAccessToken(userDetails);

        // CHỈ trả về accessToken mới, KHÔNG KN set refresh_token lại
        return ResponseEntity.ok(newAccessToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie cookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @PostMapping("/google-login")
    public ApiResponse<?> loginWithGoogle(@RequestBody UserGoogleLoginRequest userGoogleLoginRequest) {
        return ApiResponse.success(googleAuthService.loginWithGoogle(userGoogleLoginRequest.getIdToken()));
    }
    @PostMapping("/google-link")
    public void linkWithGoogle(@RequestBody UserGoogleLoginRequest userGoogleLoginRequest) {
        googleAuthService.linkGoogle(userGoogleLoginRequest.getIdToken());
    }
}
