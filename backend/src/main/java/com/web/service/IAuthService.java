/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.service;

import com.web.dto.request.auth.UserLoginRequest;
import com.web.dto.request.auth.UserRegisterRequest;
import com.web.dto.response.auth.UserDTOResponse;
import com.web.dto.response.auth.UserLoginResponse;
import com.web.entity.RefreshTokenEntity;
import java.util.Optional;

/**
 *
 * @author ZZ
 */
public interface IAuthService {
    UserDTOResponse register(UserRegisterRequest userDTO);
    UserLoginResponse login(UserLoginRequest userLoginRequest);
    UserLoginResponse refreshToken(String refreshToken);
}
