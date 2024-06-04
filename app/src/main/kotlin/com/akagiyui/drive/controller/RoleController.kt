package com.akagiyui.drive.controller

import com.akagiyui.drive.component.permission.RequirePermission
import com.akagiyui.drive.entity.User
import com.akagiyui.drive.model.Permission
import com.akagiyui.drive.model.RoleFilter
import com.akagiyui.drive.model.request.role.AddRoleRequest
import com.akagiyui.drive.model.request.role.UpdateRoleRequest
import com.akagiyui.drive.model.response.PageResponse
import com.akagiyui.drive.model.response.role.PermissionResponse
import com.akagiyui.drive.model.response.role.RoleInfoResponse
import com.akagiyui.drive.model.response.role.toResponse
import com.akagiyui.drive.service.RoleService
import com.akagiyui.drive.service.UserService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

/**
 * 角色 控制器
 *
 * @author AkagiYui
 */
@RestController
@RequestMapping("/role")
class RoleController(private val roleService: RoleService, private val userService: UserService) {

    /**
     * 获取角色信息
     *
     * @return 角色信息 列表
     */
    @GetMapping("", "/")
    @RequirePermission(Permission.ROLE_VIEW)
    @Transactional
    fun getPage(
        @RequestParam(defaultValue = "0") index: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @ModelAttribute filter: RoleFilter?,
    ): PageResponse<RoleInfoResponse> {
        val page = roleService.find(index, size, filter)
        return PageResponse(page, page.content.toResponse())
    }

    /**
     * 添加角色
     *
     * @param role 角色
     * @return 角色ID
     */
    @PostMapping("", "/")
    @RequirePermission(Permission.ROLE_ADD)
    fun addRole(@RequestBody role: AddRoleRequest): String {
        return roleService.addRole(role)
    }

    /**
     * 删除角色
     *
     * @param id 角色ID
     */
    @DeleteMapping("/{id}")
    @RequirePermission(Permission.ROLE_DELETE)
    fun deleteRole(@PathVariable("id") id: String) {
        roleService.deleteRole(id)
    }

    /**
     * 更新角色
     *
     * @param id   角色ID
     * @param role 角色
     */
    @PutMapping("/{id}")
    @RequirePermission(Permission.ROLE_UPDATE)
    fun updateRole(@PathVariable("id") id: String, @RequestBody role: UpdateRoleRequest) {
        roleService.updateRole(id, role)
    }

    /**
     * 设置角色状态
     *
     * @param id       角色ID
     * @param disabled 是否禁用
     */
    @PutMapping("/{id}/status")
    @RequirePermission(Permission.ROLE_UPDATE)
    fun updateStatus(@PathVariable("id") id: String, @RequestParam(required = false) disabled: Boolean?) {
        if (disabled != null) {
            roleService.disable(id, disabled)
        }
    }

    /**
     * 获取所有权限
     *
     * @return 权限列表
     */
    @GetMapping("/permissions")
    @PreAuthorize("permitAll()")
    fun getPermissions(): List<PermissionResponse> {
        return roleService.getAllPermissions().toResponse()
    }

    /**
     * 获取角色用户
     *
     * @param id 角色ID
     * @return 用户ID列表
     */
    @GetMapping("/{id}/users")
    @RequirePermission(Permission.USER_VIEW)
    fun getUsers(@PathVariable("id") id: String): List<String> {
        return roleService.getUsers(id).stream().map(User::id).toList()
    }

    /**
     * 分配用户
     *
     * @param id      角色ID
     * @param userIds 用户ID列表
     */
    @PutMapping("/{id}/users")
    @RequirePermission(Permission.ROLE_ASSIGN)
    fun setUsers(@PathVariable("id") id: String, @RequestBody userIds: Set<String>) {
        userIds.forEach { userId: String ->
            userService.addRoles(userId, setOf(id))
        }
    }

    /**
     * 移除用户
     *
     * @param id      角色ID
     * @param userIds 用户ID列表
     */
    @DeleteMapping("/{id}/users")
    @RequirePermission(Permission.ROLE_ASSIGN)
    fun removeUsers(@PathVariable("id") id: String, @RequestBody userIds: List<String>) {
        userIds.forEach { userId: String ->
            userService.removeRoles(userId, setOf(id))
        }
    }
}
