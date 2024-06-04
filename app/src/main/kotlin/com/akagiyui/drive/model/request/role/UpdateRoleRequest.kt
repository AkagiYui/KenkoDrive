package com.akagiyui.drive.model.request.role

import jakarta.validation.constraints.NotNull

/**
 * 更新角色 请求
 *
 * @author AkagiYui
 */
class UpdateRoleRequest {
    /**
     * 角色名
     */
    @NotNull(message = "{name is missing}")
    var name: String? = null

    /**
     * 角色描述
     */
    var description: String? = null

    /**
     * 角色权限
     */
    var permissions: Set<String>? = null

    /**
     * 是否是默认角色
     */
    var isDefault: Boolean? = null
}
