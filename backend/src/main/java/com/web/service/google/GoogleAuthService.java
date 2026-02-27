package com.web.service.google;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.web.dto.response.auth.UserLoginResponse;
import com.web.entity.CartEntity;
import com.web.entity.RoleEntity;
import com.web.entity.UserEntity;
import com.web.enums.Provider;
import com.web.enums.Role;
import com.web.exception.MyException;
import com.web.mapper.UserMapper;
import com.web.repository.RoleRepository;
import com.web.repository.UserRepository;
import com.web.security.CustomUserDetails;
import com.web.security.JwtUtils;
import com.web.security.SecurityUtil;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GoogleAuthService {

    @Value("${google.client-id}")
    private String clientId;

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    public GoogleIdToken.Payload verify(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance()
            ).setAudience(Collections.singletonList(clientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                throw new MyException("Google token không hợp lệ");
            }
            return idToken.getPayload();
        } catch (Exception e) {
            throw new MyException("xác thực token thất bại ");
        }
    }

    public UserLoginResponse loginWithGoogle(String idToken) {
        var payload = verify(idToken);
        String googleId = payload.getSubject();
        String email = payload.getEmail();
        String name = (String) payload.get("name");

        UserEntity user = userRepository.findByGoogleId(googleId);
        if (user == null) {
            user = new UserEntity();
            user.setEmail(email);
            user.setFullName(name);
            user.setGoogleId(googleId);
            user.setProvider(Provider.GOOGLE);

            RoleEntity role = roleRepository.findByName("ROLE_USER");

            CartEntity cartEntity = new CartEntity();
            cartEntity.setUser(user);

            user.getRoles().add(role);
            user.setTotalVnd(0L);
            user.setVnd(0L);
            user.setCreatedAt(LocalDateTime.now());
            user.setIsActive(true);
            user.setCart(cartEntity);
            userRepository.save(user);
        }

        CustomUserDetails userDetail = new CustomUserDetails(user);
        String accessToken = jwtUtils.generateAccessToken(userDetail);
        String freshToken = jwtUtils.generateRefreshToken(userDetail);
        UserLoginResponse userResponse = new UserLoginResponse();
        userResponse.setAccessToken(accessToken);
        userResponse.setRefreshToken(freshToken);
        userResponse.setUser(userMapper.toDTORSP(userDetail.getUser()));

        return userResponse;
    }

    public void linkGoogle(String idToken){
        Long userId = SecurityUtil.getUserId();
        UserEntity user = userRepository.findById(userId).orElseThrow(()-> new MyException("Người dùng không tồn tại "));
        var payload = verify(idToken);
        String googleId = payload.getSubject();
        if(userRepository.existsByGoogleId(googleId)){
            throw new MyException("Tài khoản google này đã liên kết với người dùng khác");
        }
        user.setGoogleId(googleId);
        userRepository.save(user);
    }
}
