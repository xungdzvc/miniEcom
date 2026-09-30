package com.web.service;

import com.web.dto.RoleDTO; 
import java.util.List;

public interface IRoleService {
    public List<RoleDTO> getRoleNotAdmin();
}
