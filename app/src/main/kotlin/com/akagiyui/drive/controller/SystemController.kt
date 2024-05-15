package com.akagiyui.drive.controller

import com.akagiyui.common.ResponseEnum
import com.akagiyui.common.exception.CustomException
import com.akagiyui.common.utils.SystemInformationUtil
import com.akagiyui.common.utils.toUnderscoreCase
import com.akagiyui.drive.component.permission.RequirePermission
import com.akagiyui.drive.entity.ActionLog
import com.akagiyui.drive.model.Permission
import com.akagiyui.drive.model.response.PageResponse
import com.akagiyui.drive.service.ActionLogService
import com.akagiyui.drive.service.SettingKey
import com.akagiyui.drive.service.SettingService
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.*


/**
 * 系统 API
 *
 * @author AkagiYui
 */
@RestController
@RequestMapping("/system")
class SystemController(
    private val settingService: SettingService,
    private val actionLogService: ActionLogService,
) {

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
     * 更新设置
     */
    @PutMapping("/setting/{key}")
    @RequirePermission(Permission.CONFIGURATION_UPDATE)
    fun updateSetting(@PathVariable key: String, @RequestParam value: String) {
        try {
            settingService.updateSetting(SettingKey.valueOf(key.toUnderscoreCase(true)), value)
        } catch (e: IllegalArgumentException) {
            throw CustomException(ResponseEnum.BAD_REQUEST)
        }
    }

    /**
     * 获取操作日志
     * @param index 页码
     * @param size 每页大小
     * @return 操作日志分页
     */
    @GetMapping("/log")
    @RequirePermission(Permission.ACTION_LOG_GET)
    fun getActionLog(
        @RequestParam(defaultValue = "0") index: Int,
        @RequestParam(defaultValue = "10") size: Int,
    ): PageResponse<ActionLog> {
        return PageResponse(actionLogService.find(index, size))
    }

    /**
     * 获取系统信息
     */
    @GetMapping("/", "")
    @RequirePermission(Permission.SYSTEM_INFO_GET)
    fun getSystemInfo(): Map<String, Any> {
        return mapOf(
            "jvm" to SystemInformationUtil.getJvmInformation(),
            "memory" to SystemInformationUtil.getMemoryInformation(),
            "system" to SystemInformationUtil.getSystemInformation(),
            "hardware" to SystemInformationUtil.getHardwareInformation(),
        )
    }
}
