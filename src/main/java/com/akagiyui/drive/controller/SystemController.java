package com.akagiyui.drive.controller;

import com.akagiyui.drive.component.permission.RequirePermission;
import com.akagiyui.drive.model.Permission;
import com.akagiyui.drive.model.request.EnableConfigRequest;
import com.akagiyui.drive.service.ConfigService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 系统 API
 *
 * @author AkagiYui
 */
@RestController
@RequestMapping("/system")
public class SystemController {
    @Value("${application.version:unknown}")
    private String version;
    private final ConfigService configService;

    public SystemController(ConfigService configService) {
        this.configService = configService;
    }

    /**
     * 获取系统版本
     */
    @RequestMapping("/version")
    public String getVersion() {
        return version;
    }

    /**
     * 获取服务器设置
     */
    @RequestMapping("/config")
    public Map<String, Object> getConfig() {
        return configService.getConfig();
    }

    /**
     * 是否开放注册
     */
    @GetMapping("/config/register")
    @PreAuthorize("permitAll()")
    public boolean isRegisterEnabled() {
        return configService.isRegisterEnabled();
    }

    /**
     * 更新 是否开放注册
     */
    @PutMapping("/config/register")
    @RequirePermission(Permission.CONFIGURATION_UPDATE)
    public void setRegisterEnabled(@Validated @RequestBody EnableConfigRequest request) {
        configService.setRegisterEnabled(request.getEnabled());
    }
}
