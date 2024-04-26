package com.akagiyui.drive.controller

import com.akagiyui.drive.component.permission.RequirePermission
import com.akagiyui.drive.model.Permission
import com.akagiyui.drive.model.request.EnableConfigRequest
import com.akagiyui.drive.service.SettingService
import org.springframework.beans.factory.annotation.Value
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

/**
 * 系统 API
 *
 * @author AkagiYui
 */
@RestController
@RequestMapping("/system")
class SystemController(private val settingService: SettingService) {

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
    @RequestMapping("/setting")
    @RequirePermission(Permission.CONFIGURATION_GET)
    fun getSetting(): Map<String, Any> {
        return settingService.getSettings()
    }

    /**
     * 是否开放注册
     */
    @GetMapping("/setting/register")
    fun isRegisterEnabled(): Boolean {
        return settingService.registerEnabled
    }

    /**
     * 更新 是否开放注册
     */
    @PutMapping("/setting/register")
    @RequirePermission(Permission.CONFIGURATION_UPDATE)
    fun setRegisterEnabled(@Validated @RequestBody request: EnableConfigRequest) {
        settingService.registerEnabled = request.enabled
    }
}
