package com.akagiyui.drive.model.response

import com.akagiyui.drive.entity.Role

/**
 * 角色信息响应
 *
 * @author AkagiYui
 */
data class RoleInfoResponse(
    /**
     * 角色ID
     */
    val id: String,

    /**
     * 角色名
     */
    val name: String,

    /**
     * 角色描述
     */
    val description: String,

    /**
     * 角色是否被禁用
     */
    val disabled: Boolean,

    /**
     * 是否是默认角色
     */
    val isDefault: Boolean,

    /**
     * 角色用户数
     */
    val userCount: Long,

    /**
     * 角色权限
     */
    val permissions: List<String>,
) {
    constructor(role: Role) : this(
        id = role.id,
        name = role.name,
        description = role.description ?: "",
        disabled = role.disabled,
        isDefault = role.isDefault,
        userCount = role.users.size.toLong(),
        permissions = role.permissions.map { it.name }
    )
}

/**
 * 从角色列表转换
 *
 * @return 角色信息响应列表
 */
fun List<Role>.toResponse(): List<RoleInfoResponse> {
    return this.map { RoleInfoResponse(it) }.toList()
}
