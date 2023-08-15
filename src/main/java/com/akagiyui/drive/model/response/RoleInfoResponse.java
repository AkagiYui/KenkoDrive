package com.akagiyui.drive.model.response;

import com.akagiyui.drive.entity.Role;
import lombok.Data;
import lombok.experimental.Accessors;

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
     * 从角色实体转换
     */
    public static RoleInfoResponse fromRole(Role role) {
        return new RoleInfoResponse()
                .setId(role.getId())
                .setName(role.getName())
                .setDescription(role.getDescription());
    }

}
