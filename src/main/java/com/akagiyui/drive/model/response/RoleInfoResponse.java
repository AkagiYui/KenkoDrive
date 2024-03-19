package com.akagiyui.drive.model.response;

import com.akagiyui.drive.entity.Role;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 角色信息响应
 *
 * @author AkagiYui
 */
@Data
@Accessors(chain = true)
public class RoleInfoResponse {

    /**
     * 角色id
     */
    private String id;

    /**
     * 角色名
     */
    private String name;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 角色是否被禁用
     */
    private boolean disabled;

    public RoleInfoResponse(Role role) {
        this.id = role.getId();
        this.name = role.getName();
        this.description = role.getDescription();
        this.disabled = role.getDisabled();
    }

    /**
     * 从角色列表转换
     *
     * @param roles 角色列表
     * @return 角色信息响应列表
     */
    public static List<RoleInfoResponse> fromRoleList(List<Role> roles) {
        return roles.stream().map(RoleInfoResponse::new).toList();
    }

}
