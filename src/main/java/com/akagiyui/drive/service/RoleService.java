package com.akagiyui.drive.service;

import com.akagiyui.drive.entity.Role;
import com.akagiyui.drive.model.Permission;
import com.akagiyui.drive.model.filter.RoleFilter;
import com.akagiyui.drive.model.request.AddRoleRequest;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 角色 Service 接口
 *
 * @author AkagiYui
 */
public interface RoleService {

    /**
     * 获取所有角色
     */
    List<Role> getAllRoles();

    /**
     * 获取所有默认角色
     */
    List<Role> getAllDefaultRoles();

    /**
     * 分页查询角色
     *
     * @param index  页码
     * @param size   页大小
     * @param filter 查询条件
     * @return 角色分页
     */
    Page<Role> find(Integer index, Integer size, RoleFilter filter);

    /**
     * 获取所有权限
     *
     * @return 权限列表
     */
    List<Permission> getAllPermissions();

    /**
     * 添加角色
     *
     * @param role 角色
     */
    void addRole(AddRoleRequest role);

    /**
     * 删除角色
     *
     * @param id 角色id
     */
    void deleteRole(String id);
}
