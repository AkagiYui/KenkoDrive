package com.akagiyui.drive.controller

import com.akagiyui.drive.component.permission.RequirePermission
import com.akagiyui.drive.model.Permission
import com.akagiyui.drive.model.request.EnableConfigRequest
import com.akagiyui.drive.service.ConfigService
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

/**
 * 系统 API
 *
 * @author AkagiYui
 */
@RestController
@RequestMapping("/system")
class SystemController(private val configService: ConfigService) {

    @Value("\${application.version:unknown}")
    lateinit var version: String

    /**
     * 获取系统版本
     */
    @RequestMapping("/version")
    fun getSystemVersion(): String {
        return version
    }

    /**
     * 获取服务器设置
     */
    @RequestMapping("/config")
    fun getConfig(): Map<String, Any> {
        return configService.getConfig()
    }

    /**
     * 是否开放注册
     */
    @GetMapping("/config/register")
    @PreAuthorize("permitAll()")
    fun isRegisterEnabled(): Boolean {
        return configService.isRegisterEnabled()
    }

    /**
     * 更新 是否开放注册
     */
    @PutMapping("/config/register")
    @RequirePermission(Permission.CONFIGURATION_UPDATE)
    fun setRegisterEnabled(@Validated @RequestBody request: EnableConfigRequest) {
        configService.setRegisterEnabled(request.enabled)
    }
}
