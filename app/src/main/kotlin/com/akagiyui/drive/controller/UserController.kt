package com.akagiyui.drive.controller

import com.akagiyui.drive.component.CurrentUser
import com.akagiyui.drive.component.permission.RequirePermission
import com.akagiyui.drive.entity.Role
import com.akagiyui.drive.entity.User
import com.akagiyui.drive.model.Permission
import com.akagiyui.drive.model.UserFilter
import com.akagiyui.drive.model.request.user.AddUserRequest
import com.akagiyui.drive.model.request.user.ResetPasswordRequest
import com.akagiyui.drive.model.request.user.UpdateUserInfoRequest
import com.akagiyui.drive.model.response.PageResponse
import com.akagiyui.drive.model.response.user.UserInfoResponse
import com.akagiyui.drive.model.response.user.toResponse
import com.akagiyui.drive.model.toModel
import com.akagiyui.drive.service.AvatarService
import com.akagiyui.drive.service.UserService
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.stream.Collectors

/**
 * 用户 API
 *
 * @author AkagiYui
 */
@RestController
@RequestMapping("/user")
class UserController(private val userService: UserService, private val avatarService: AvatarService) {
    /**
     * 根据用户ID查找用户
     *
     * @param id 用户ID
     * @return 用户
     */
    @GetMapping("/{id}")
    @RequirePermission(Permission.USER_VIEW)
    fun findById(@PathVariable("id") id: String): User {
        return userService.getUserById(id)
    }

    /**
     * 新增用户
     *
     * @param user 用户
     * @return 用户ID
     */
    @PostMapping("", "/")
    @RequirePermission(Permission.USER_ADD)
    fun add(@Validated @RequestBody user: AddUserRequest): String {
        return userService.addUser(user.toModel()).id
    }

    /**
     * 分页查询用户
     *
     * @param index 页码
     * @param size  每页大小
     * @return 用户分页响应
     */
    @GetMapping("", "/")
    @RequirePermission(Permission.USER_VIEW)
    fun getPage(
        @RequestParam(defaultValue = "0") index: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @ModelAttribute filter: UserFilter?,
    ): PageResponse<UserInfoResponse> {
        val page = userService.getUsers(index, size, filter)
        return PageResponse(page, page.content.toResponse())
    }

    /**
     * 根据 ID 删除用户
     *
     * @param id 用户 ID
     */
    @DeleteMapping("/{id}")
    @RequirePermission(Permission.USER_DELETE)
    fun delete(@PathVariable id: String) {
        userService.deleteUser(id)
    }

    /**
     * 启用/禁用用户
     *
     * @param id       用户 ID
     * @param disabled 是否禁用
     */
    @PutMapping("/{id}/disable")
    @RequirePermission(Permission.USER_UPDATE)
    fun disable(@PathVariable id: String, @RequestParam disabled: Boolean) {
        userService.disable(id, disabled)
    }

    /**
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/info")
    @RequirePermission
    fun getUserInfo(@CurrentUser user: User): UserInfoResponse {
        return UserInfoResponse(user)
    }

    /**
     * 更新当前用户信息
     */
    @PutMapping("/info")
    @RequirePermission
    fun updateLoginUserInfo(@RequestBody @Validated userInfo: UpdateUserInfoRequest, @CurrentUser user: User) {
        userService.updateInfo(user.id, userInfo)
    }

    /**
     * 更新用户信息
     *
     * @param id      用户ID
     * @param userInfo 用户信息
     */
    @PutMapping("/{id}")
    @RequirePermission(Permission.USER_UPDATE)
    fun updateUserInfo(@PathVariable id: String, @RequestBody @Validated userInfo: UpdateUserInfoRequest) {
        userService.updateInfo(id, userInfo)
    }

    /**
     * 获取用户角色
     *
     * @param id 用户ID
     * @return 角色列表
     */
    @GetMapping("/{id}/role")
    @RequirePermission(Permission.USER_VIEW)
    fun getRoles(@PathVariable id: String): Set<String> {
        val roles = userService.getRoles(id)
        return roles.stream().map(Role::id).collect(Collectors.toSet())
    }

    /**
     * 上传头像
     *
     * @param avatar 头像文件
     */
    @PostMapping("/avatar")
    @RequirePermission
    fun updateAvatar(@RequestParam("avatar") avatar: MultipartFile, @CurrentUser user: User) {
        avatarService.saveAvatar(user.id, avatar)
    }

    /**
     * 获取头像
     *
     * @return 头像
     */
    @GetMapping("/avatar")
    @RequirePermission
    fun getAvatar(@CurrentUser user: User): ResponseEntity<ByteArray> {
        val avatar = avatarService.getAvatar(user.id)

        val header = HttpHeaders()
        header.contentType = MediaType.valueOf("image/" + avatar.contentType)

        return ResponseEntity
            .ok()
            .headers(header)
            .body(avatar.content)
    }

    /**
     * 获取当前用户权限列表
     */
    @GetMapping("/permission")
    @RequirePermission
    fun getPermission(@CurrentUser user: User): Set<String> {
        return userService.getPermission(user)
    }

    /**
     * 获取当前用户角色列表
     */
    @GetMapping("/role")
    @RequirePermission
    fun getRole(@CurrentUser user: User): Set<String> {
        return userService.getRole(user)
    }

    /**
     * (管理员)重置密码
     */
    @PutMapping("/{id}/password")
    @RequirePermission(Permission.USER_UPDATE)
    fun resetPassword(@PathVariable id: String, @RequestBody @Validated request: ResetPasswordRequest) {
        userService.resetPassword(id, request.newPassword)
    }

    /**
     * 分配角色
     *
     * @param id      用户ID
     * @param roleIds 角色ID列表
     */
    @PutMapping("/{id}/role")
    @RequirePermission(Permission.ROLE_ASSIGN)
    fun setUsers(@PathVariable("id") id: String, @RequestBody roleIds: Set<String>) {
        userService.addRoles(id, roleIds)
    }

    /**
     * 移除角色
     *
     * @param id      用户ID
     * @param roleIds 角色ID列表
     */
    @DeleteMapping("/{id}/role")
    @RequirePermission(Permission.ROLE_ASSIGN)
    fun removeUsers(@PathVariable("id") id: String, @RequestBody roleIds: Set<String>) {
        userService.removeRoles(id, roleIds)
    }
}
