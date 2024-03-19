package com.akagiyui.drive.service.impl;

import com.akagiyui.common.ResponseEnum;
import com.akagiyui.common.exception.CustomException;
import com.akagiyui.drive.entity.Role;
import com.akagiyui.drive.model.Permission;
import com.akagiyui.drive.model.filter.RoleFilter;
import com.akagiyui.drive.model.request.AddRoleRequest;
import com.akagiyui.drive.repository.RoleRepository;
import com.akagiyui.drive.service.RoleService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 角色服务实现类
 *
 * @author kenko
 */
@Service
@Slf4j
public class RoleServiceImpl implements RoleService {

    @Resource
    private RoleRepository roleRepository;

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public List<Role> getAllDefaultRoles() {
        return roleRepository.findAllByIsDefaultIsTrue();
    }

    @Override
    public Page<Role> find(Integer index, Integer size, RoleFilter filter) {
        Pageable pageable = PageRequest.of(index, size);
        return roleRepository.findAll(pageable);
    }

    @Override
    public List<Permission> getAllPermissions() {
        return List.of(Permission.values());
    }

    @Override
    public boolean addRole(AddRoleRequest role) {
        // 检查角色名是否重复
        if (roleRepository.existsByName(role.getName())) {
            log.warn("角色名重复: {}", role.getName());
            throw new CustomException(ResponseEnum.ROLE_EXIST);
        }
        Set<Permission> permissions;
        // 检查权限是否存在
        try {
            permissions = role.getPermissions().stream()
                .map(Permission::valueOf)
                .collect(Collectors.toSet());
        } catch (IllegalArgumentException e) {
            log.warn("权限不存在: {}", role.getPermissions());
            throw new CustomException(ResponseEnum.PERMISSION_NOT_EXIST);
        }
        // 添加角色
        Role newRole = new Role()
            .setName(role.getName())
            .setDescription(role.getDescription())
            .setPermissions(permissions);
        roleRepository.save(newRole);
        return true;
    }

}
