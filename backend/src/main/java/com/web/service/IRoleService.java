package com.web.service;

import com.web.dto.RoleDTO;
import com.web.entity.RoleEntity;
import java.util.List;

public interface IRoleService {
    public List<RoleDTO> getAllRole();
}
