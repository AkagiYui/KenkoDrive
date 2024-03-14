package com.akagiyui.drive.controller

import com.akagiyui.drive.service.ConfigService
import jakarta.annotation.Resource
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.*

/**
 * 服务器 API
 * @author AkagiYui
 */
@RestController
@RequestMapping("/server")
open class ServerController(
    @Value("\${application.version:unknown}")
    private val version: String,
    private val configService: ConfigService,
) {
    /**
     * 获取服务器版本
     * @return 服务器版本
     */
    @RequestMapping("/version")
    open fun getVersion(): String? {
        return version
    }

    /**
     * 是否开放注册
     */
    @GetMapping("/config/is-register-enabled")
    open fun getConfig(): Boolean {
        return configService.isRegisterEnabled()
    }

    /**
     * 设置 是否开放注册
     */
    @PutMapping("/config/is-register-enabled")
    open fun setConfig(@RequestParam enabled: Boolean) {
        configService.setRegisterEnabled(enabled)
    }
}
