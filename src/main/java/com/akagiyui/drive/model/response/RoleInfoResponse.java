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

    public RoleInfoResponse(Role role) {
        this.id = role.getId();
        this.name = role.getName();
        this.description = role.getDescription();
    }

    public static List<RoleInfoResponse> fromRoleList(List<Role> roles) {
        return roles.stream().map(RoleInfoResponse::new).toList();
    }

}
