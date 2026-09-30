package com.web.service.impl;

import com.web.dto.PasswordDTO;
import com.web.dto.UserAdminEditDTO;
import com.web.dto.request.user.UserUpdateRequest;
import com.web.dto.request.user.ChangeStatusRequest;
import com.web.dto.response.user.UserAdminListResponse;
import com.web.entity.RoleEntity;
import com.web.entity.UserEntity;
import com.web.exception.MyException;
import com.web.mapper.UserMapper;
import com.web.repository.RoleRepository;
import com.web.repository.UserRepository;
import com.web.security.CustomUserDetails;
import com.web.service.IUserService;
import com.web.util.Utils;
import jdk.jshell.execution.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.web.dto.response.auth.UserDTOResponse;
import com.web.dto.response.user.UserProfileResponse;
import com.web.enums.Role;
import com.web.security.SecurityUtil;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final Clock clock;
    private final PasswordEncoder passwordEncoder;

    @Transactional

    @Override
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        return customUserDetails.getUserId();
    }

    @Override
    public UserDTOResponse delete(Long id) {
        UserEntity userEntity = userRepository.findById(id).orElseThrow(() -> new MyException("Dùng dùng không tồn tại"));

        userEntity.setIsActive(false);
        userRepository.save(userEntity);
        return userMapper.toDTORSP(userEntity);

    }

    @Override
    public UserDTOResponse updatePassword(Long id, PasswordDTO passwordDTO) {
        UserEntity userEntity = userRepository.findById(id).orElseThrow(() -> new MyException("Người dùng không tồn tại"));
        if (!passwordDTO.getNewPassword().equals(passwordDTO.getConfirmPassword())) {
            throw new MyException("Mật khẩu xác nhận không khớp");
        }

        if (!passwordEncoder.matches(passwordDTO.getOldPassword(), userEntity.getPassword())) {
            throw new MyException("Mật khẩu hiện tại không chính xác");
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
    @Retryable(
            retryFor = {ObjectOptimisticLockingFailureException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 100, multiplier = 2)
    )
    @Transactional
    public void deposit(Long userId, BigDecimal amount) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new MyException("Tài khoản không tồn tại"));
        userEntity.deposit(amount);
        userRepository.save(userEntity);
    }

    @Override
    public int getCount() {
        return (int) userRepository.count();
    }

    @Override
    public UserDTOResponse getUserProfileById(Long userId) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new MyException("Người dùng không tồn tại"));
        return userMapper.toDTORSP(userEntity);
    }

    @Override
    public UserAdminEditDTO getUserById(Long userId) {
        UserEntity userE = userRepository.findById(userId).orElseThrow(() -> new MyException("Người dùng không tồn tại"));
        UserAdminEditDTO dto = userMapper.toUserAdminEditDTO(userE);
        dto.setRoleIds(new ArrayList<>());
        for (RoleEntity role : userE.getRoles()) {
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
        validateRole(userId);
        
        if (dto.getRoleIds() != null) {
            List<RoleEntity> roles = new ArrayList<>();
            for (Long id : dto.getRoleIds()) { 
                RoleEntity role = roleRepository.findById(id).orElseThrow(() -> new MyException("Role không tồn tại"));
                if(role.getName().contains(Role.ADMIN.roleName())){
                    throw new MyException("Bạn không thể cấu hình quyền Admin cho người dùng khác");
                }
                roles.add(role);
            }
            
            userE.setRoles(roles);
        }
        userMapper.updateEntityFromDto(dto, userE);
        userRepository.save(userE);

    }
    
    public void validateRole(Long userId){
        if(Objects.equals(userId, SecurityUtil.getUserId())){
            throw new MyException("Bạn không thể sửa quyền của chính mình");
        } 
        if(!SecurityUtil.isAdmin()){
            throw new MyException("Bạn không có quyền thực hiện thao tác này");
        }
    }

    @Override
    public int countNewUsersToday() {
        LocalDate today = LocalDate.now(clock.withZone(Utils.getInstance().getZoneId()));
        Instant startTime = today
                .atStartOfDay(Utils.getInstance().getZoneId())
                .toInstant();

        Instant endTime = today
                .plusDays(1)
                .atStartOfDay(Utils.getInstance().getZoneId())
                .toInstant();

        return userRepository.countByCreatedAtBetween(startTime, endTime);
    }

    @Override
    public int countNewUsersMonth() {
        LocalDate today = LocalDate.now(clock.withZone(Utils.getInstance().getZoneId()));
        LocalDate firstDayOfMonth =
                today.withDayOfMonth(1);

        LocalDate firstDayOfNextMonth =
                firstDayOfMonth.plusMonths(1);
        Instant startTime = firstDayOfMonth
                .atStartOfDay(Utils.getInstance().getZoneId())
                .toInstant();

        Instant endTime = firstDayOfNextMonth
                .atStartOfDay(Utils.getInstance().getZoneId())
                .toInstant();
        return userRepository.countByCreatedAtBetween(startTime, endTime);
    }

    @Recover
    public void handleDepositFailure(ObjectOptimisticLockingFailureException e) {
        throw new MyException("Hệ thống đang có lượng giao dịch quá cao, Vui lòng chờ");
    }

    @Override 
    @Retryable(
            retryFor = {ObjectOptimisticLockingFailureException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 100, multiplier = 2)
    )
    @Transactional
    public void wallet(Long userId, BigDecimal amount) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new MyException("Tài khoản không tồn tại"));
        userEntity.wallet(amount);
        userRepository.save(userEntity);
    }

}
