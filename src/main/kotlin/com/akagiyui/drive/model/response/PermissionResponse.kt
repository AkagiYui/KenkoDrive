package com.akagiyui.drive.model.response

import com.akagiyui.drive.model.Permission

/**
 * 权限响应
 *
 * @author AkagiYui
 */
class PermissionResponse(permission: Permission) {
    /**
     * 权限名
     */
    var name = permission.name

    /**
     * 权限描述
     */
    var description = permission.description

    companion object {
        /**
         * 从权限列表转换
         *
         * @param permissions 权限列表
         * @return 权限响应列表
         */
        fun fromPermissionList(permissions: List<Permission>): List<PermissionResponse> {
            return permissions
                .stream()
                .map { permission -> PermissionResponse(permission) }
                .toList()
        }
    }
}
