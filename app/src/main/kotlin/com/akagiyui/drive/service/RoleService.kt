package com.akagiyui.drive.service

import com.akagiyui.drive.entity.Role
import com.akagiyui.drive.entity.User
import com.akagiyui.drive.model.Permission
import com.akagiyui.drive.model.RoleFilter
import com.akagiyui.drive.model.request.role.AddRoleRequest
import com.akagiyui.drive.model.request.role.UpdateRoleRequest
import org.springframework.data.domain.Page

/**
 * 角色 Service 接口
 *
 * @author AkagiYui
 */
interface RoleService {

    /**
     * 获取所有角色
     */
    fun getAllRoles(): List<Role>

    /**
     * 获取所有默认角色
     */
    fun getAllDefaultRoles(): MutableSet<Role>

    /**
     * 分页查询角色
     *
     * @param index  页码
     * @param size   页大小
     * @param filter 查询条件
     * @return 角色分页
     */
    fun find(index: Int, size: Int, filter: RoleFilter?): Page<Role>

    /**
     * 根据ID查找角色
     *
     * @param ids 角色ID
     * @return 角色
     */
    fun find(ids: Set<String>): Set<Role>

    /**
     * 获取所有权限
     *
     * @return 权限列表
     */
    fun getAllPermissions(): List<Permission>

    /**
     * 添加角色
     *
     * @param role 角色
     * @return 角色ID
     */
    fun addRole(role: AddRoleRequest): String

    /**
     * 添加角色
     *
     * @param role 角色
     * @return 角色
     */
    fun addRole(role: Role): Role

    /**
     * 删除角色
     *
     * @param id 角色ID
     */
    fun deleteRole(id: String)

    /**
     * 更新角色
     *
     * @param id   角色ID
     * @param role 角色
     */
    fun updateRole(id: String, role: UpdateRoleRequest)

    /**
     * 禁用角色
     *
     * @param id       角色ID
     * @param disabled 是否禁用
     */
    fun disable(id: String, disabled: Boolean)

    /**
     * 获取角色用户
     *
     * @param id 角色ID
     * @return 用户列表
     */
    fun getUsers(id: String): Set<User>

    /**
     * 根据角色id查找用户ID
     *
     * @param id 角色ID
     * @return 用户ID列表
     */
    fun findUserIdsById(id: String): List<String>

}
