/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.service.impl;

import com.web.dto.request.auth.UserLoginRequest;
import com.web.dto.request.auth.UserRegisterRequest;
import com.web.dto.response.auth.UserDTOResponse;
import com.web.dto.response.auth.UserLoginResponse;
import com.web.entity.CartEntity;
import com.web.entity.RefreshTokenEntity;
import com.web.entity.UserEntity;
import com.web.enums.Provider;
import com.web.exception.MyException;
import com.web.mapper.UserMapper;
import com.web.repository.RefreshTokenRepository;
import com.web.repository.RoleRepository;
import com.web.repository.UserRepository;
import com.web.security.CustomUserDetails;
import com.web.security.JwtUtils;
import com.web.service.IAuthService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 *
 * @author ZZ
 */
@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public UserDTOResponse register(UserRegisterRequest userDTO) {
        
        validateUserRegisterRequest(userDTO);
        
        UserEntity userEntity = createUser(userDTO);
        createCart(userEntity);

        userRepository.save(userEntity);

        return userMapper.toDTORSP(userEntity);

    }

    @Override
    public UserLoginResponse login(UserLoginRequest userLoginRequest) {

        validateUserLoginRequest(userLoginRequest);

        UsernamePasswordAuthenticationToken authToken
                = new UsernamePasswordAuthenticationToken(userLoginRequest.getUsername(), userLoginRequest.getPassword());

        Authentication authentication = authenticationManager.authenticate(authToken);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String accessToken = jwtUtils.generateAccessToken(userDetails);
        String refreshToken = jwtUtils.generateRefreshToken(userDetails);
        UserLoginResponse userLoginResponse = new UserLoginResponse();
        userLoginResponse.setAccessToken(accessToken);
        userLoginResponse.setRefreshToken(refreshToken);

        String jti = jwtUtils.getJti(refreshToken);

        RefreshTokenEntity refreshTokenEntity = createRefreshTokenEntity(userDetails.getUser(), jti);
        refreshTokenRepository.save(refreshTokenEntity);
        userLoginResponse.setUser(userMapper.toDTORSP(userDetails.getUser()));
        return userLoginResponse;

    }

    @Override
    public UserLoginResponse refreshToken(String refreshToken) {
        validationRefreshToken(refreshToken);
        String jti = jwtUtils.getJti(refreshToken);
        RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.findByJti(jti).orElseThrow(() -> new MyException("Lỗi refreshToken"));
        validationRefreshToken(refreshTokenEntity);

        UserEntity userEntity = refreshTokenEntity.getUser();
        CustomUserDetails userDetails = new CustomUserDetails(userEntity);
        String newAccessToken = jwtUtils.generateAccessToken(userDetails);
        String newRefreshToken = jwtUtils.generateRefreshToken(userDetails);

        String newJti = jwtUtils.getJti(newRefreshToken);
        RefreshTokenEntity newRefreshTokenEntity = createRefreshTokenEntity(userEntity, newJti);



        refreshTokenEntity.setReplacedByJti(newJti);
        refreshTokenEntity.setRevoked(true);
        refreshTokenEntity.setRevokedAt(Instant.now());

        refreshTokenRepository.save(refreshTokenEntity);
        refreshTokenRepository.save(newRefreshTokenEntity);

        UserLoginResponse userLoginResponse = new UserLoginResponse();
        userLoginResponse.setAccessToken(newAccessToken);
        userLoginResponse.setRefreshToken(newRefreshToken);
        userLoginResponse.setUser(userMapper.toDTORSP(userDetails.getUser()));

        return userLoginResponse;
    }

    @Override
    public void logout(String refreshToken) {
        String jti = jwtUtils.getJti(refreshToken);
        RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.findByJti(jti).orElseThrow(() -> new MyException("Lỗi refreshToken Logut"));
        if (refreshTokenEntity.isRevoked()) {
            throw new MyException("Token đã bị đóng");
        }
        refreshTokenEntity.setRevoked(true);
        refreshTokenEntity.setRevokedAt(Instant.now());
        refreshTokenRepository.save(refreshTokenEntity);
    }

    private void validateUserRegisterRequest(UserRegisterRequest userRegisterRequest) {
        userRegisterRequest.validate();
        if (userRepository.existsByEmail(userRegisterRequest.getEmail())) {
            throw new MyException("Email này đã được sử dụng");
        }
        if (userRepository.existsByUsername(userRegisterRequest.getUsername())) {
            throw new MyException("Tài khoản này đã tồn tại");
        }
    }

    private UserEntity createUser(UserRegisterRequest userRegisterRequest) {
        UserEntity user = new UserEntity();
        user.setUsername(userRegisterRequest.getUsername());
        user.setEmail(userRegisterRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRegisterRequest.getPassword()));
        user.setProvider(Provider.LOCAL);
        user.getRoles().add(roleRepository.findByName("ROLE_USER"));
        return user;
    }

    public CartEntity createCart(UserEntity userEntity) {
        CartEntity cartEntity = new CartEntity();
        cartEntity.setUser(userEntity);
        return cartEntity;
    }

    private void validateUserLoginRequest(UserLoginRequest userLoginRequest) {

        if (!userRepository.existsByUsername(userLoginRequest.getUsername())) {
            throw new MyException("Thông tin tài khoản hoặc mật khẩu không chính xác");
        }
        if(!passwordEncoder.matches(userLoginRequest.getPassword(),userRepository.findByUsername(userLoginRequest.getUsername()).getPassword())){
            throw new MyException("Thông tin tài khoản hoặc mật khẩu không chính xác");
        }

    }

    public RefreshTokenEntity createRefreshTokenEntity(UserEntity userEntity, String jti) { 
        RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity();
        refreshTokenEntity.setJti(jti);
        refreshTokenEntity.setUser(userEntity);  
        refreshTokenEntity.setRevoked(false);
        refreshTokenEntity.setExpiredAt(Instant.now().plus(7, ChronoUnit.DAYS));
        return refreshTokenEntity;
    }

    private void validationRefreshToken(RefreshTokenEntity refreshTokenEntity) {
        if (refreshTokenEntity.isRevoked()) {
            throw new MyException("Token đã bị đóng");
        } 
        if (refreshTokenEntity.getExpiredAt().isBefore(Instant.now())) {
            throw new MyException("Token đã hết hạn");
        }
    }

    private void validationRefreshToken(String refreshToken) {
        if (refreshToken.isEmpty()) {
            throw new MyException("Token rỗng");
        }
    }

}
