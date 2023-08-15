package com.akagiyui.drive.controller;

import com.akagiyui.drive.model.response.RoleInfoResponse;
import com.akagiyui.drive.service.RoleService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 元信息控制器
 *
 * @author AkagiYui
 */
@RestController
@RequestMapping("/info")
public class MetaInfoController {

    @Value("${application.version:unknown}")
    private String version;

    @Resource
    private RoleService roleService;

    /**
     * 获取服务器版本
     *
     * @return 服务器版本
     */
    @RequestMapping("/version")
    public String getVersion() {
        return version;
    }

    /**
     * 获取角色信息
     */
    @RequestMapping("/roles")
    public List<RoleInfoResponse> getRoles() {
        return roleService.getAllRoles();
    }
}
