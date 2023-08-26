package com.akagiyui.drive.controller;

import com.akagiyui.drive.model.response.RoleInfoResponse;
import com.akagiyui.drive.service.RoleService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author AkagiYui
 */
@RestController
@RequestMapping("/role")
public class RoleController {

    @Resource
    private RoleService roleService;

    /**
     * 获取角色信息
     */
    @RequestMapping({"/", ""})
    public List<RoleInfoResponse> getRoles() {
        return roleService.getAllRoles();
    }

}
