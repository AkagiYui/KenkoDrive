package com.akagiyui.drive.controller;

import com.akagiyui.drive.model.response.RoleInfoResponse;
import com.akagiyui.drive.service.RoleService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 角色 控制器
 *
 * @author AkagiYui
 */
@RestController
@RequestMapping("/role")
public class RoleController {

    @Resource
    private RoleService roleService;

    /**
     * 获取角色信息
     *
     * @return 角色信息 列表
     */
    @RequestMapping({"/", ""})
    @PreAuthorize("permitAll()")
    public List<RoleInfoResponse> getRoles() {
        return roleService.getAllRoles();
    }

}
