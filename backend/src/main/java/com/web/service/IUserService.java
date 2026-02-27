package com.web.service;

import com.web.dto.PasswordDTO;
import com.web.dto.UserDTO;
import com.web.dto.request.auth.UserCreateRequest;
import com.web.dto.request.auth.UserLoginRequest;
import com.web.dto.request.user.UserUpdateRequest;
import com.web.dto.request.user.ChangeStatusRequest;
import com.web.dto.response.auth.UserDTOResponse;
import com.web.dto.response.auth.UserLoginResponse;
import com.web.dto.response.user.UserAdminListResponse;
import com.web.dto.response.user.UserProfileResponse;
import com.web.entity.UserEntity;

import java.util.List;

public interface IUserService {
    UserDTOResponse register(UserCreateRequest userDTO);
    UserLoginResponse login(UserLoginRequest userLoginRequest);
    UserDTOResponse update(Long Id,UserUpdateRequest userDTO);
    void changeStatus(Long Id, ChangeStatusRequest req);
    Long getCurrentUserId();
    UserDTOResponse delete(Long Id);
    UserDTOResponse updatePassword(Long id, PasswordDTO passworDTO);
    UserProfileResponse updateProfile(Long id, UserUpdateRequest userDTO);
    List<UserAdminListResponse> getUers();
    UserDTOResponse getUserProfileById(Long id);
    int getCount();

    UserEntity getUserById(Long userId);
}
