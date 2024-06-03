package com.akagiyui.drive.model.response

import com.akagiyui.drive.entity.User

/**
 * 用户信息响应
 *
 * @author AkagiYui
 */
data class UserInfoResponse(
    /**
     * 用户ID
     */
    val id: String,

    /**
     * 用户名
     */
    val username: String,

    /**
     * 昵称
     */
    val nickname: String,

    /**
     * 邮箱
     */
    val email: String,

    /**
     * 已禁用
     */
    val disabled: Boolean,

    /**
     * 注册时间
     */
    val registerTime: Long,

    /**
     * 权限
     */
    val permissions: List<String>,
) {
    constructor(user: User) : this(
        id = user.id,
        username = user.username,
        nickname = user.nickname ?: user.username,
        email = user.email ?: "",
        disabled = user.disabled,
        registerTime = user.createTime.time,
        permissions = user.roles.flatMap { it.permissions }.map { it.name }
    )
}

/**
 * 从用户列表转换
 *
 * @return 用户信息响应列表
 */
fun List<User>.toResponse(): List<UserInfoResponse> {
    return this.map { UserInfoResponse(it) }.toList()
}
