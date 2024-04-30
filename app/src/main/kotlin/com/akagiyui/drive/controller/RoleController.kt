package com.akagiyui.drive.controller

import com.akagiyui.drive.component.permission.RequirePermission
import com.akagiyui.drive.entity.User
import com.akagiyui.drive.model.Permission
import com.akagiyui.drive.model.RoleFilter
import com.akagiyui.drive.model.request.AddRoleRequest
import com.akagiyui.drive.model.request.UpdateRoleRequest
import com.akagiyui.drive.model.response.PageResponse
import com.akagiyui.drive.model.response.PermissionResponse
import com.akagiyui.drive.model.response.RoleInfoResponse
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
        val rolePage = roleService.find(index, size, filter)
        val roleList = rolePage.content
        val roleResponseList = RoleInfoResponse.fromRoleList(roleList)

        return PageResponse<RoleInfoResponse>().apply {
            this.page = index
            this.size = size
            this.pageCount = rolePage.totalPages
            this.total = rolePage.totalElements
            this.list = roleResponseList
        }
    }

    /**
     * 添加角色
     *
     * @param role 角色
     * @return 角色id
     */
    @PostMapping("", "/")
    @RequirePermission(Permission.ROLE_ADD)
    fun addRole(@RequestBody role: AddRoleRequest): String {
        return roleService.addRole(role)
    }

    /**
     * 删除角色
     *
     * @param id 角色id
     */
    @DeleteMapping("/{id}")
    @RequirePermission(Permission.ROLE_DELETE)
    fun deleteRole(@PathVariable("id") id: String) {
        roleService.deleteRole(id)
    }

    /**
     * 更新角色
     *
     * @param id   角色id
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
     * @param id       角色id
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
        return PermissionResponse.fromPermissionList(roleService.getAllPermissions())
    }

    /**
     * 获取角色用户
     *
     * @param id 角色id
     * @return 用户id列表
     */
    @GetMapping("/{id}/users")
    @RequirePermission(Permission.USER_VIEW)
    fun getUsers(@PathVariable("id") id: String): List<String> {
        return roleService.getUsers(id).stream().map(User::id).toList()
    }

    /**
     * 分配用户
     *
     * @param id      角色id
     * @param userIds 用户id列表
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
     * @param id      角色id
     * @param userIds 用户id列表
     */
    @DeleteMapping("/{id}/users")
    @RequirePermission(Permission.ROLE_ASSIGN)
    fun removeUsers(@PathVariable("id") id: String, @RequestBody userIds: List<String>) {
        userIds.forEach { userId: String ->
            userService.removeRoles(userId, setOf(id))
        }
    }
}
