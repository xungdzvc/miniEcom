package com.web.service;

import com.web.dto.RoleDTO;
import com.web.entity.RoleEntity;

public interface IRoleService {
    public RoleDTO addRole(RoleEntity roleEntity);
}
