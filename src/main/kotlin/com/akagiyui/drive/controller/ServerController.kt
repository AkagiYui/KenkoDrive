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
class ServerController {
    @Value("\${application.version:unknown}")
    private val version: String? = null

    @Resource
    private val configService: ConfigService? = null

    /**
     * 获取服务器版本
     * @return 服务器版本
     */
    @RequestMapping("/version")
    fun getVersion(): String? {
        return version
    }

    /**
     * 是否开放注册
     */
    @GetMapping("/config/is-register-enabled")
    fun getConfig(): Boolean {
        return configService?.isRegisterEnabled() == true
    }

    /**
     * 设置 是否开放注册
     */
    @PutMapping("/config/is-register-enabled")
    fun setConfig(@RequestParam enabled: Boolean) {
        configService?.setRegisterEnabled(enabled)
    }
}
