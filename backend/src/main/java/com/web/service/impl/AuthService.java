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
import com.web.entity.RoleEntity;
import com.web.entity.UserEntity;
import com.web.exception.MyException;
import com.web.mapper.UserMapper;
import com.web.repository.RefreshTokenRepository;
import com.web.repository.RoleRepository;
import com.web.repository.UserRepository;
import com.web.security.CustomUserDetails;
import com.web.security.JwtUtils;
import com.web.service.IAuthService;
import java.time.LocalDateTime;
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
public class AuthService implements IAuthService{
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenRepository refreshTokenRepository;
    
    @Override
    public UserDTOResponse register(UserRegisterRequest userDTO) {
        if (!userDTO.getRetype_password().endsWith(userDTO.getPassword())) {
            throw new MyException("Mật khẩu bạn nhập không khớp");
        }
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new MyException("Email này đã được sử dụng bởi tài khoản khác");
        }
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new MyException("Tài khoản này đã được sử dụng");
        }
        UserEntity userEntity = userMapper.toEntity(userDTO);
        userEntity.setCreatedAt(LocalDateTime.now());

        RoleEntity role = roleRepository.findByName("ROLE_USER");

        CartEntity cartEntity = new CartEntity();
        cartEntity.setUser(userEntity);

        userEntity.getRoles().add(role);
        userEntity.setAddress("");
        userEntity.setTotalVnd(0L);
        userEntity.setVnd(0L);
        userEntity.setCreatedAt(LocalDateTime.now());
        userEntity.setIsActive(true);
        userEntity.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        userEntity.setUsername(userDTO.getUsername());
        userEntity.setCart(cartEntity);
        userRepository.save(userEntity);
        return userMapper.toDTORSP(userEntity);

    }

    @Override
    public UserLoginResponse login(UserLoginRequest userLoginRequest) {
        UsernamePasswordAuthenticationToken authToken
                = new UsernamePasswordAuthenticationToken(userLoginRequest.getUsername(), userLoginRequest.getPassword());
     
        Authentication authentication = authenticationManager.authenticate(authToken);
     
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String accessToken = jwtUtils.generateAccessToken(userDetails);
        String refreshToken = jwtUtils.generateRefreshToken(userDetails);
        
        UserLoginResponse user = new UserLoginResponse();
        user.setAccessToken(accessToken);
        user.setRefreshToken(refreshToken);
        
        RefreshTokenEntity freshE = new RefreshTokenEntity();
        LocalDateTime now = LocalDateTime.now();
        freshE.setJti(jwtUtils.getJti(refreshToken));
        freshE.setUser(userDetails.getUser());
        freshE.setCreatedAt(now);
        freshE.setExpiredAt(now.plusDays(7));
        freshE.setRevoked(false);
        
        refreshTokenRepository.save(freshE);
        
        
        user.setUser(userMapper.toDTORSP(userDetails.getUser()));
        return user;
    }

    @Override
    public UserLoginResponse refreshToken(String refreshToken) {
        String jti = jwtUtils.getJti(refreshToken);
        RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.findByJti(jti).orElseThrow(()-> new MyException("Lỗi refreshToken"));
        if(refreshTokenEntity.isRevoked()){
            throw new MyException("Token đã hết hạn");
        }
        LocalDateTime now = LocalDateTime.now();
        if(refreshTokenEntity.getExpiredAt().isAfter(now)){
            throw new MyException("Token đã hết hạn");
        }
        
        refreshTokenEntity.setReplacedByJti(jti);
        refreshTokenEntity.setRevoked(true);
        refreshTokenEntity.setRevokedAt(now);
        
        UserEntity userE = refreshTokenEntity.getUser();
        CustomUserDetails userDetails = new CustomUserDetails(userE);
        String newAccessToken = jwtUtils.generateAccessToken(userDetails);
        String newRefreshToken = jwtUtils.generateRefreshToken(userDetails);
        
        
        RefreshTokenEntity newRefreshTokenEntity = new RefreshTokenEntity();
        newRefreshTokenEntity.setCreatedAt(now);
        newRefreshTokenEntity.setExpiredAt(now.plusDays(7));
        newRefreshTokenEntity.setUser(userE);
        newRefreshTokenEntity.setJti(jti);
        
        refreshTokenRepository.save(refreshTokenEntity);
        refreshTokenRepository.save(newRefreshTokenEntity);
        
        UserLoginResponse userLoginResponse = new UserLoginResponse();
        userLoginResponse.setAccessToken(newAccessToken);
        userLoginResponse.setRefreshToken(newRefreshToken);
        userLoginResponse.setUser(userMapper.toDTORSP(userDetails.getUser()));
        
        
        return userLoginResponse;
    }
    
}
