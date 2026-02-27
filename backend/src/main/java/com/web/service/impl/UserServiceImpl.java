package com.web.service.impl;

import com.web.dto.PasswordDTO;
import com.web.dto.request.auth.UserCreateRequest;
import com.web.dto.request.auth.UserLoginRequest;
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

import java.time.LocalDateTime;
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
    public List<UserAdminListResponse> getUers() {
        List<UserEntity> userEntities = userRepository.findAll();
        return userEntities.stream().map(userMapper::toUserAdminListResponse).toList();
    }

    @Override
    public UserDTOResponse register(UserCreateRequest userDTO) {
        if (!userDTO.getRetype_password().endsWith(userDTO.getPassword())) {
            throw new MyException("Mật khẩu bạn nhập không khớp");
        }
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new MyException("Email này đã được sử dụng bở tài khoản khác");
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
        // Authenticate → SPRING SECURITY sẽ tạo CustomUserDetails
        Authentication authentication = authenticationManager.authenticate(authToken);
        // Lấy principal TỪ authentication (KHÔNG PHẢI từ authToken)
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String accessToken = jwtUtils.generateAccessToken(userDetails);
        String refreshToken = jwtUtils.generateRefreshToken(userDetails);
        UserLoginResponse user = new UserLoginResponse();
        user.setAccessToken(accessToken);
        user.setRefreshToken(refreshToken);
        user.setUser(userMapper.toDTORSP(userDetails.getUser()));
        return user;
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
    public UserEntity getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new MyException("Người dùng không tồn tại"));
    }
}
