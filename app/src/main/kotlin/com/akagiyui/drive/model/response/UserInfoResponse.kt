package com.akagiyui.drive.model.response

import com.akagiyui.drive.entity.User

/**
 * 用户信息响应
 *
 * @author AkagiYui
 */
class UserInfoResponse(user: User) {
    /**
     * 用户ID
     */
    val id = user.id

    /**
     * 用户名
     */
    val username: String? = user.username

    /**
     * 昵称
     */
    val nickname = user.nickname

    /**
     * 邮箱
     */
    val email = user.email

    /**
     * 已禁用
     */
    val disabled: Boolean = user.disabled

    /**
     * 注册时间
     */
    val registerTime = user.createTime

    /**
     * 权限
     */
    val permissions: List<String>

    init {
        val roles = user.roles
        val allPermissions = roles.stream().flatMap { it.permissions.stream() }.toList()
        this.permissions = allPermissions.stream().map { it.name }.toList()
    }

    companion object {
        fun fromUserList(users: List<User>): List<UserInfoResponse> {
            return users
                .stream()
                .map { UserInfoResponse(it) }
                .toList()
        }
    }
}
