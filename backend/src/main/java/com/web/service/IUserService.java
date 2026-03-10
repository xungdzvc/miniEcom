package com.web.service;

import com.web.dto.PasswordDTO;
import com.web.dto.UserAdminEditDTO;
import com.web.dto.UserDTO;
import com.web.dto.request.auth.UserRegisterRequest;
import com.web.dto.request.auth.UserLoginRequest;
import com.web.dto.request.user.UserUpdateRequest;
import com.web.dto.request.user.ChangeStatusRequest;
import com.web.dto.request.user.ChangeStaffRequest;
import com.web.dto.response.auth.UserDTOResponse;
import com.web.dto.response.auth.UserLoginResponse;
import com.web.dto.response.user.UserAdminListResponse;
import com.web.dto.response.user.UserProfileResponse;
import com.web.entity.UserEntity;

import java.util.List;

public interface IUserService {
    UserDTOResponse update(Long Id,UserUpdateRequest userDTO);
    void changeStatus(Long Id, ChangeStatusRequest req);
    Long getCurrentUserId();
    UserDTOResponse delete(Long Id);
    UserDTOResponse updatePassword(Long id, PasswordDTO passworDTO);
    UserProfileResponse updateProfile(Long id, UserUpdateRequest userDTO);
    List<UserAdminListResponse> getUsers();
    UserDTOResponse getUserProfileById(Long id);
    int getCount();
    UserAdminEditDTO getUserById(Long userId);
    UserEntity getUserById(long userId);
    void updateUser(Long userId,UserAdminEditDTO dto);
    int countNewUsersToday();
    int countNewUsersMonth();
    
}
