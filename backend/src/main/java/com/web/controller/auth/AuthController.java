package com.web.controller.auth;

import com.web.dto.UserLoginResultDTO;
import com.web.dto.request.auth.UserRegisterRequest;
import com.web.dto.request.auth.UserGoogleLoginRequest;
import com.web.dto.request.auth.UserLoginRequest;
import com.web.dto.response.auth.UserDTOResponse;
import com.web.exception.TooManyRequestsException;
import com.web.security.ratelimit.RateLimited;
import com.web.service.IUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.web.dto.response.auth.UserLoginResponse;
import com.web.dto.response.common.ApiResponse;
import com.web.security.SecurityUtil;
import com.web.service.IAuthService;
import com.web.service.google.GoogleAuthService;
import com.web.service.ratelimit.LoginRateLimitService;
import com.web.util.Utils;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.BadCredentialsException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;
    private final IUserService userService;
    private final GoogleAuthService googleAuthService;
    private final LoginRateLimitService loginRateLimitService;

    @RateLimited("register")
    @PostMapping("/register")
    public ApiResponse<?> register(
            @Valid @RequestBody UserRegisterRequest request) {
        UserDTOResponse userResponse = authService.register(request);
        return ApiResponse.success(userResponse, "Đăng ký thành công");
    }

    @GetMapping("/me")
    public ApiResponse<?> getProfile() {
        Long id = SecurityUtil.getUserId();
        return ApiResponse.success(userService.getUserProfileById(id), "Lấy dữ liệu người dùng thành công");
    }
    @RateLimited("login")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginRequest userLoginRequest,
            HttpServletRequest request) {
        String ip = Utils.getClientIp(request);
        String username = userLoginRequest.getUsername();

        if (loginRateLimitService.isBlocked(ip, username)) {
            throw new TooManyRequestsException("Bạn đã đăng hập sai quá nhiều lần vui lòng thử lại sau ít phút",5);
        }

        try {

            UserLoginResponse userLoginResponse = authService.login(userLoginRequest);
            loginRateLimitService.loginSuccess(ip, username);
            return buildAuthResponse(userLoginResponse);
        } catch (BadCredentialsException e) {
            loginRateLimitService.loginFailed(ip, username);
            throw e;
        } 
    }
    @RateLimited("refresh-token")
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refresh(
            @CookieValue(value = "refresh_token", required = false) String refreshToken) {
        UserLoginResponse userLoginResponse = authService.refreshToken(refreshToken);
        return buildAuthResponse(userLoginResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(value = "refresh_token", required = false) String refreshToken) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            authService.logout(refreshToken);
        }
        ResponseCookie cookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @PostMapping("/google-login")
    public ResponseEntity<?> loginWithGoogle(@Valid @RequestBody UserGoogleLoginRequest userGoogleLoginRequest,
            HttpServletRequest request) {

        String ip = Utils.getClientIp(request);
        String username = userGoogleLoginRequest.getIdToken();

        if (loginRateLimitService.isBlocked(ip, username)) {
            throw new TooManyRequestsException("Bạn đã đăng hập sai quá nhiều lần vui lòng thử lại sau ít phút",5);
        }

        try {

            UserLoginResponse userLoginResponse = googleAuthService.loginWithGoogle(userGoogleLoginRequest.getIdToken());
            loginRateLimitService.loginSuccess(ip, username);
            return buildAuthResponse(userLoginResponse);
        } catch (BadCredentialsException e) {
            loginRateLimitService.loginFailed(ip, username);
            throw e;
        }
    }

    @PostMapping("/google-link")
    public void linkWithGoogle(@Valid @RequestBody UserGoogleLoginRequest userGoogleLoginRequest) {
        googleAuthService.linkGoogle(userGoogleLoginRequest.getIdToken());
    }

    private ResponseEntity<UserLoginResultDTO> buildAuthResponse(UserLoginResponse result) {
        UserLoginResultDTO dto = new UserLoginResultDTO();
        dto.setAccessToken(result.getAccessToken());
        dto.setUser(result.getUser());

        ResponseCookie cookie = ResponseCookie.from("refresh_token", result.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(dto);
    }
}
