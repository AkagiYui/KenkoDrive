package com.akagiyui.drive.model.response;

import com.akagiyui.drive.model.Permission;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 权限响应
 *
 * @author AkagiYui
 */
@Data
@Accessors(chain = true)
public class PermissionResponse {
    /**
     * 权限名
     */
    private String name;

    /**
     * 权限描述
     */
    private String description;

    public PermissionResponse(Permission permission) {
        this.name = permission.name();
        this.description = permission.getDescription();
    }

    /**
     * 从权限列表转换
     *
     * @param permissions 权限列表
     * @return 权限响应列表
     */
    public static List<PermissionResponse> fromPermissionList(List<Permission> permissions) {
        return permissions.stream().map(PermissionResponse::new).toList();
    }

}
