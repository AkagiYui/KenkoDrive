package com.akagiyui.drive.service.impl;

import com.akagiyui.common.ResponseEnum;
import com.akagiyui.common.exception.CustomException;
import com.akagiyui.drive.entity.Role;
import com.akagiyui.drive.entity.User;
import com.akagiyui.drive.model.Permission;
import com.akagiyui.drive.model.filter.RoleFilter;
import com.akagiyui.drive.model.request.AddRoleRequest;
import com.akagiyui.drive.model.request.UpdateRoleRequest;
import com.akagiyui.drive.repository.RoleRepository;
import com.akagiyui.drive.service.RoleService;
import jakarta.annotation.Resource;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
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

        // 条件查询
        Specification<Role> specification = (root, query, cb) -> {
            if (filter != null && StringUtils.hasText(filter.getExpression())){
                Predicate namePredicate = cb.like(root.get("name"), "%" + filter.getExpression() + "%");
                Predicate descriptionPredicate = cb.like(root.get("description"), "%" + filter.getExpression() + "%");
                return cb.or(namePredicate, descriptionPredicate);
            }
            return null;
        };

        return roleRepository.findAll(specification, pageable);
    }

    @Override
    public List<Permission> getAllPermissions() {
        return List.of(Permission.values());
    }

    @Override
    public String addRole(AddRoleRequest role) {
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
        // 设置默认角色
        if (role.getIsDefault() != null) {
            newRole.setIsDefault(role.getIsDefault());
        }
        roleRepository.save(newRole);
        return newRole.getId();
    }

    @Override
    public void deleteRole(String id) {
        // 检查角色是否存在
        if (!roleRepository.existsById(id)) {
            log.warn("角色不存在: {}", id);
            throw new CustomException(ResponseEnum.ROLE_NOT_EXIST);
        }
        roleRepository.deleteById(id);
    }

    @Override
    public void updateRole(String id, UpdateRoleRequest newRole) {
        // 检查角色是否存在
        Role oldRole = roleRepository.findById(id).orElseThrow(() -> {
            log.warn("角色不存在: {}", id);
            return new CustomException(ResponseEnum.ROLE_NOT_EXIST);
        });
        // 修改角色名
        if (StringUtils.hasText(newRole.getName()) && !Objects.equals(oldRole.getName(), newRole.getName())) {
            if (roleRepository.existsByName(newRole.getName())) {
                log.warn("角色名重复: {}", newRole.getName());
                throw new CustomException(ResponseEnum.ROLE_EXIST);
            }
            oldRole.setName(newRole.getName());
        }
        // 修改角色描述
        if (StringUtils.hasText(newRole.getDescription()) && !Objects.equals(oldRole.getDescription(), newRole.getDescription())) {
            oldRole.setDescription(newRole.getDescription());
        }
        // 检查 默认角色 是否被修改
        if (newRole.getIsDefault() != null && !Objects.equals(oldRole.getIsDefault(), newRole.getIsDefault())) {
            oldRole.setIsDefault(newRole.getIsDefault());
        }
        // 修改权限
        if (newRole.getPermissions() != null) {
            Set<Permission> permissions;
            try {
                permissions = newRole.getPermissions().stream()
                    .map(Permission::valueOf)
                    .collect(Collectors.toSet());
            } catch (IllegalArgumentException e) {
                log.warn("权限不存在: {}", newRole.getPermissions());
                throw new CustomException(ResponseEnum.PERMISSION_NOT_EXIST);
            }
            oldRole.setPermissions(permissions);
        }
        roleRepository.save(oldRole);
    }

    @Override
    public void disable(String id, boolean disabled) {
        // 检查角色是否存在
        Role role = roleRepository.findById(id).orElseThrow(() -> {
            log.warn("角色不存在: {}", id);
            return new CustomException(ResponseEnum.ROLE_NOT_EXIST);
        });
        if (!Objects.equals(role.getDisabled(), disabled)) {
            role.setDisabled(disabled);
            roleRepository.save(role);
        }
    }

    @Override
    @Transactional
    public Set<User> getUsers(String id) {
        // 检查角色是否存在
        Role role = roleRepository.findById(id).orElseThrow(() -> {
            log.warn("角色不存在: {}", id);
            return new CustomException(ResponseEnum.ROLE_NOT_EXIST);
        });
        Hibernate.initialize(role.getUsers()); // 初始化 LAZY 属性
        return role.getUsers();
    }

    @Override
    public List<String> findUserIdsById(String id) {
        return roleRepository.findUserIdsById(id);
    }

}
