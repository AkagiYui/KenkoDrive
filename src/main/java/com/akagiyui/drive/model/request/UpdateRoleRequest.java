package com.akagiyui.drive.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

/**
 * 更新角色 请求
 *
 * @author AkagiYui
 */
@Data
public class UpdateRoleRequest {
    /**
     * 角色名
     */
    @NotNull(message = "{name is missing}")
    private String name;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 角色权限
     */
    private Set<String> permissions;

    /**
     * 是否是默认角色
     */
    private Boolean isDefault;

}
