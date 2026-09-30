package com.web.service.google;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.web.dto.request.auth.UserRegisterRequest;
import com.web.dto.response.auth.UserLoginResponse;
import com.web.entity.CartEntity;
import com.web.entity.RefreshTokenEntity;
import com.web.entity.RoleEntity;
import com.web.entity.UserEntity;
import com.web.enums.Provider;
import com.web.exception.MyException;
import com.web.mapper.UserMapper;
import com.web.repository.RefreshTokenRepository;
import com.web.repository.RoleRepository;
import com.web.repository.UserRepository;
import com.web.security.CustomUserDetails;
import com.web.security.JwtUtils;
import com.web.security.SecurityUtil;
import com.web.service.impl.AuthService;
import java.io.IOException;
import java.security.GeneralSecurityException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleAuthService {

    @Value("${google.client-id}")
    private String clientId;

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthService authService;

    public GoogleIdToken.Payload verify(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance()
            ).setAudience(Collections.singletonList(clientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                throw new AuthenticationException("Google token không hợp lệ") {
                };
            }
            return idToken.getPayload();
        } catch (GeneralSecurityException e) {
            throw new AuthenticationException("xác thực token thất bại ") {
            };
        } catch (IOException ex) {
            Logger.getLogger(GoogleAuthService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public UserLoginResponse loginWithGoogle(String idToken) {
        UserEntity userEntity = createUser(idToken);
        CustomUserDetails userDetail = new CustomUserDetails(userEntity);
        String accessToken = jwtUtils.generateAccessToken(userDetail);
        String freshToken = jwtUtils.generateRefreshToken(userDetail);

        RefreshTokenEntity freshE = authService.createRefreshTokenEntity(userEntity, jwtUtils.getJti(freshToken));

        refreshTokenRepository.save(freshE);

        UserLoginResponse userResponse = new UserLoginResponse();
        userResponse.setAccessToken(accessToken);
        userResponse.setRefreshToken(freshToken);
        userResponse.setUser(userMapper.toDTORSP(userDetail.getUser()));

        return userResponse;
    }

    public void linkGoogle(String idToken) {
        Long userId = SecurityUtil.getUserId();
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new MyException("Người dùng không tồn tại "));
        var payload = verify(idToken);
        String googleId = payload.getSubject();
        if (userRepository.existsByGoogleId(googleId)) {
            throw new MyException("Tài khoản google này đã liên kết với người dùng khác");
        }
        user.setGoogleId(googleId);
        userRepository.save(user);
    }

    private UserEntity createUser(String idToken) {
        var payload = verify(idToken);
        String googleId = payload.getSubject();
        String email = payload.getEmail();
        String name = (String) payload.get("name");

        UserEntity userEntity = userRepository.findByGoogleId(googleId);
        if (userEntity == null) {
            userEntity = new UserEntity();
            userEntity.setEmail(email);
            userEntity.setFullName(name);
            userEntity.setGoogleId(googleId);
            userEntity.setProvider(Provider.GOOGLE);
            userEntity.getRoles().add(roleRepository.findByName("ROLE_USER"));
            userRepository.save(userEntity);
        }
        authService.createCart(userEntity);
        return userEntity;
    }

}
