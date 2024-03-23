package com.akagiyui.drive.service;

import com.akagiyui.drive.entity.Role;
import com.akagiyui.drive.entity.User;
import com.akagiyui.drive.model.Permission;
import com.akagiyui.drive.model.filter.RoleFilter;
import com.akagiyui.drive.model.request.AddRoleRequest;
import com.akagiyui.drive.model.request.UpdateRoleRequest;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Set;

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
     * 根据id查找角色
     *
     * @param ids 角色id
     * @return 角色
     */
    Set<Role> find(Set<String> ids);

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
     * @return 角色id
     */
    String addRole(AddRoleRequest role);

    /**
     * 删除角色
     *
     * @param id 角色id
     */
    void deleteRole(String id);

    /**
     * 更新角色
     *
     * @param id   角色id
     * @param role 角色
     */
    void updateRole(String id, UpdateRoleRequest role);

    /**
     * 禁用角色
     *
     * @param id       角色id
     * @param disabled 是否禁用
     */
    void disable(String id, boolean disabled);

    /**
     * 获取角色用户
     *
     * @param id 角色id
     * @return 用户列表
     */
    Set<User> getUsers(String id);

    /**
     * 根据角色id查找用户id
     *
     * @param id 角色id
     * @return 用户id列表
     */
    List<String> findUserIdsById(String id);
}
