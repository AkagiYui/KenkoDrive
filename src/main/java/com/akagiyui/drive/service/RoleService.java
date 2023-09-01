package com.akagiyui.drive.service;

import com.akagiyui.drive.entity.Role;
import com.akagiyui.drive.model.response.RoleInfoResponse;

import java.util.List;

/**
 * 角色 Service 接口
 * @author AkagiYui
 */
public interface RoleService {

    /**
     * 获取所有角色
     */
    List<RoleInfoResponse> getAllRoles();

    /**
     * 获取所有默认角色
     */
    List<Role> getAllDefaultRoles();
}
