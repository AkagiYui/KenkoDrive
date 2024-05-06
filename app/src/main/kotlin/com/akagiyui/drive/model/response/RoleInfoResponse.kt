package com.akagiyui.drive.model.response

import com.akagiyui.drive.entity.Role

/**
 * 角色信息响应
 *
 * @author AkagiYui
 */
class RoleInfoResponse(role: Role) {
    /**
     * 角色ID
     */
    val id = role.id

    /**
     * 角色名
     */
    val name: String = role.name

    /**
     * 角色描述
     */
    val description = role.description

    /**
     * 角色是否被禁用
     */
    val disabled: Boolean = role.disabled

    /**
     * 是否是默认角色
     */
    val isDefault: Boolean = role.isDefault

    /**
     * 角色用户数
     */
    val userCount = role.users.size.toLong()

    /**
     * 角色权限
     */
    val permissions: List<String> =
        role.permissions.stream().map { it.name }.toList()

    companion object {
        /**
         * 从角色列表转换
         *
         * @param roles 角色列表
         * @return 角色信息响应列表
         */
        fun fromRoleList(roles: List<Role>): List<RoleInfoResponse> {
            return roles
                .stream()
                .map { role -> RoleInfoResponse(role) }
                .toList()
        }
    }
}
