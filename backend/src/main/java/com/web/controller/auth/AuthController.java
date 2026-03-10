package com.web.controller.auth;

import com.web.dto.UserLoginResultDTO;
import com.web.dto.request.auth.UserRegisterRequest;
import com.web.dto.request.auth.UserGoogleLoginRequest;
import com.web.dto.request.auth.UserLoginRequest;
import com.web.dto.response.auth.UserDTOResponse;
import com.web.security.JwtUtils;
import com.web.service.IUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.web.dto.response.auth.UserLoginResponse;
import com.web.dto.response.common.ApiResponse;
import com.web.entity.UserEntity;
import com.web.security.CustomUserDetails;
import com.web.security.SecurityUtil;
import com.web.service.IAuthService;
import com.web.service.google.GoogleAuthService;
import lombok.RequiredArgsConstructor;

;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;
    private final IUserService userService;
    private final GoogleAuthService googleAuthService;

    private final JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody UserRegisterRequest request,
            BindingResult bindingResult
    ) {

        if (bindingResult.hasErrors()) {

            String message = bindingResult.getFieldErrors()
                    .get(0)
                    .getDefaultMessage();

            return ResponseEntity.badRequest().body(message);
        }

        UserDTOResponse userResponse = authService.register(request);

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
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginRequest userLoginRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String message = bindingResult.getFieldErrors()
                    .get(0)
                    .getDefaultMessage();
            return ResponseEntity.badRequest().body(message);
        }
        UserLoginResponse userLoginResponse = authService.login(userLoginRequest);

        return buildAuthResponse(userLoginResponse);
    }

    @PostMapping("/fresh-token")
    public ResponseEntity<?> refresh(
            @CookieValue("refresh_token") String refreshToken) {

        UserLoginResponse userLoginResponse = authService.refreshToken(refreshToken);

        return buildAuthResponse(userLoginResponse);
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

    private ResponseEntity<UserLoginResultDTO> buildAuthResponse(UserLoginResponse result) {
        UserLoginResultDTO dto = new UserLoginResultDTO();
        dto.setAccessToken(result.getAccessToken());
        dto.setUser(result.getUser());

        ResponseCookie cookie = ResponseCookie.from("refresh_token", result.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(dto);
    }
}
