package com.web.mapper;

import com.web.dto.UserAdminEditDTO;
import com.web.dto.request.auth.UserRegisterRequest;
import com.web.dto.request.user.UserUpdateRequest;
import com.web.dto.response.auth.UserDTOResponse;
import com.web.dto.response.user.UserAdminListResponse;
import com.web.dto.response.user.UserProfileResponse;
import com.web.entity.UserEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "retype_password", ignore = true)
    UserRegisterRequest toDTO(UserEntity userEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "phoneNumber", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "bankAccounts", ignore = true)
    @Mapping(target = "fullName", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "totalVnd", ignore = true)
    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "vnd", ignore = true)
    @Mapping(target = "googleId", ignore = true)
    @Mapping(target = "provider", ignore = true)
    UserEntity toEntity(UserRegisterRequest userDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "bankAccounts", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "googleId", ignore = true)
    @Mapping(target = "provider", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateEntityFromDto(UserAdminEditDTO dto, @MappingTarget UserEntity entity);

    @Mapping(target = "roleIds",ignore = true)
    UserAdminEditDTO toUserAdminEditDTO(UserEntity userEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "bankAccounts", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "totalVnd", ignore = true)
    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "vnd", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "googleId", ignore = true)
    @Mapping(target = "provider", ignore = true)
    UserEntity toEntity(UserUpdateRequest userDTO);

    @Mapping(target = "active", source = "isActive")
    @Mapping(target = "roles",
            expression = "java(userEntity.getRoles().stream().map(role->role.getName())"
            + ".collect(java.util.stream.Collectors.joining(\",\")))")
    UserDTOResponse toDTORSP(UserEntity userEntity);

    @Mapping(target = "status", source = "isActive")
    @Mapping(target = "role",
            expression = "java(userEntity.getRoles().stream().map(role->role.getName())"
            + ".collect(java.util.stream.Collectors.joining(\",\")))")
    UserAdminListResponse toUserAdminListResponse(UserEntity userEntity);

    UserProfileResponse toUserProfileResponse(UserEntity userEntity);
}
