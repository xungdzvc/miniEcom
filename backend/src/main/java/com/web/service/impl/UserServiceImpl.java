package com.web.service.impl;

import com.web.dto.PasswordDTO;
import com.web.dto.UserAdminEditDTO;
import com.web.dto.request.auth.UserRegisterRequest;
import com.web.dto.request.auth.UserLoginRequest;
import com.web.dto.request.user.ChangeStaffRequest;
import com.web.dto.request.user.UserUpdateRequest;
import com.web.dto.request.user.ChangeStatusRequest;
import com.web.dto.response.user.UserAdminListResponse;
import com.web.entity.CartEntity;
import com.web.entity.RoleEntity;
import com.web.entity.UserEntity;
import com.web.exception.MyException;
import com.web.mapper.UserMapper;
import com.web.repository.RoleRepository;
import com.web.repository.UserRepository;
import com.web.security.CustomUserDetails;
import com.web.security.JwtUtils;
import com.web.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.web.dto.response.auth.UserLoginResponse;
import com.web.dto.response.auth.UserDTOResponse;
import com.web.dto.response.auth.UserLoginResponse;
import com.web.dto.response.user.UserProfileResponse;
import com.web.entity.RefreshTokenEntity;
import com.web.enums.Role;
import java.time.LocalDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Transactional
    @Override
    public UserDTOResponse update(Long id, UserUpdateRequest userDTO) {
        UserEntity userEntity = userRepository.findById(id).orElseThrow(() -> new MyException("Người dùng không tồn tại"));
        userEntity = userMapper.toEntity(userDTO);
        userRepository.save(userEntity);
        return userMapper.toDTORSP(userEntity);
    }

    @Override
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        return customUserDetails.getUserId();
    }

    @Override
    public UserDTOResponse delete(Long id) {
        UserEntity userEntity = userRepository.findById(id).get();
        if (userEntity != null) {
            userEntity.setIsActive(false);
            userRepository.save(userEntity);
            return userMapper.toDTORSP(userEntity);
        } else {
            throw new MyException(userEntity.getUsername() + " not exists");
        }
    }

    @Override
    public UserDTOResponse updatePassword(Long id, PasswordDTO passwordDTO) {
        UserEntity userEntity = userRepository.findById(id).orElseThrow(() -> new MyException("Người dùng không tồn tại"));
        if (!passwordEncoder.matches(passwordDTO.getOldPassword(), userEntity.getPassword())) {
            throw new MyException("password not correct");
        }

        if (!passwordDTO.getNewPassword().equals(passwordDTO.getConfirmPassword())) {
            throw new MyException("password not match");
        }
        userEntity.setPassword(passwordEncoder.encode(passwordDTO.getNewPassword()));
        userRepository.save(userEntity);
        return userMapper.toDTORSP(userEntity);
    }

    @Override
    public UserProfileResponse updateProfile(Long id, UserUpdateRequest userDTO) {
        UserEntity userEntity = userRepository.findById(id).orElseThrow(() -> new MyException("Dùng dùng không tồn tại"));

        userEntity.setFullName(userDTO.getFullName());
        userEntity.setPhoneNumber(userDTO.getPhoneNumber());
        userRepository.save(userEntity);
        return userMapper.toUserProfileResponse(userEntity);
    }

    @Override
    public List<UserAdminListResponse> getUsers() {
        List<UserEntity> userEntities = userRepository.findAll();
        return userEntities.stream().map(userMapper::toUserAdminListResponse).toList();
    }

    

    @Override
    public void changeStatus(Long Id, ChangeStatusRequest req) {
        UserEntity userEntity = userRepository.findById(Id).orElseThrow(() -> new MyException("Tài khoản không tồn tại"));
        userEntity.setIsActive(req.isStatus());
        userRepository.save(userEntity);
    }

    

    @Override
    public int getCount() {
        return (int) userRepository.count();
    }

    @Override
    public UserDTOResponse getUserProfileById(Long id) {
        UserEntity userEntity = userRepository.findById(id).orElseThrow(() -> new MyException("Người dùng không tồn tại"));
        return userMapper.toDTORSP(userEntity);
    }

    @Override
    public UserAdminEditDTO getUserById(Long userId) {
        UserEntity userE = userRepository.findById(userId).orElseThrow(() -> new MyException("Người dùng không tồn tại"));
        UserAdminEditDTO dto = userMapper.toUserAdminEditDTO(userE);
        dto.setRoleIds(new ArrayList<>());
        for(RoleEntity role :userE.getRoles() ){
            dto.getRoleIds().add(role.getId());
        }
        return dto;
    }

    @Override
    public UserEntity getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new MyException("Người dùng không tồn tại"));
    }

    @Override
    public void updateUser(Long userId, UserAdminEditDTO dto) {
        
        UserEntity userE = userRepository.findById(userId).orElseThrow(() -> new MyException("Người dùng không tồn tại"));
        if(dto.getRoleIds() != null){
            List<RoleEntity> roles = new ArrayList<>();
            for(Long id :dto.getRoleIds()){
                roles.add(roleRepository.findById(id).get());
            }

            userE.setRoles(roles);
        }
        userMapper.updateEntityFromDto(dto, userE);
        userRepository.save(userE);

    }

    @Override
    public int countNewUsersToday() {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = LocalDateTime.now();
        return userRepository.countByCreatedAtBetween(start, end);
    }

    @Override
    public int countNewUsersMonth() {
        LocalDate now = LocalDate.now();
        LocalDateTime start = now.withDayOfMonth(1).atStartOfDay();
        LocalDateTime end = LocalDateTime.now();
        return userRepository.countByCreatedAtBetween(start, end);
    }
}
