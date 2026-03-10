/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.service.impl;

import com.web.dto.RoleDTO;
import com.web.entity.RoleEntity;
import com.web.repository.RoleRepository;
import com.web.service.IRoleService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 *
 * @author ZZ
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements IRoleService {

    private final RoleRepository roleRepository;

    @Override
    public List<RoleDTO> getAllRole() {
        List<RoleEntity> roles = roleRepository.findAll();
        List<RoleDTO> allRole = new ArrayList<>();
        for(RoleEntity role : roles){
            RoleDTO dto = new RoleDTO();
            dto.setId(role.getId());
            dto.setName(role.getName());
            allRole.add(dto);
        }
        return allRole;
    }

}
