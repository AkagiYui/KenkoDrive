package com.akagiyui.drive.model.response.user

import com.akagiyui.common.utils.hasText
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
     * 是否有密码
     */
    val hasPassword: Boolean,

    /**
     * 昵称
     */
    val nickname: String,

    /**
     * 邮箱
     */
    val email: String,

    /**
     * 手机号
     */
    val phone: String,

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
        nickname = user.nickname,
        hasPassword = user.password.hasText(),
        email = user.email ?: "",
        phone = user.phone ?: "",
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
