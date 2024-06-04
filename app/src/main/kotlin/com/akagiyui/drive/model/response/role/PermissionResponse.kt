package com.akagiyui.drive.model.response.role

import com.akagiyui.drive.model.Permission

/**
 * 权限响应
 *
 * @author AkagiYui
 */
data class PermissionResponse(
    /**
     * 权限名
     */
    val name: String,

    /**
     * 权限描述
     */
    val description: String,
) {
    constructor(permission: Permission) : this(
        name = permission.name,
        description = permission.description
    )
}


/**
 * 从权限列表转换
 *
 * @return 权限响应列表
 */
fun List<Permission>.toResponse(): List<PermissionResponse> {
    return this.map { PermissionResponse(it) }.toList()
}
