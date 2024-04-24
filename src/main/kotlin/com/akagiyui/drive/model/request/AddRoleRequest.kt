package com.akagiyui.drive.model.request

import jakarta.validation.constraints.NotNull

/**
 * 添加角色 请求
 *
 * @author AkagiYui
 */
class AddRoleRequest {
    /**
     * 角色名
     */
    @NotNull(message = "{name is missing}")
    lateinit var name: String

    /**
     * 角色描述
     */
    lateinit var description: String

    /**
     * 角色权限
     */
    lateinit var permissions: Set<String>

    /**
     * 是否是默认角色
     */
    var isDefault: Boolean = false
}
