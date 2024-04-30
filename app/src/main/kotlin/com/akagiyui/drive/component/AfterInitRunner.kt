package com.akagiyui.drive.component

import com.akagiyui.common.delegate.LoggerDelegate
import com.akagiyui.drive.service.SettingService
import com.akagiyui.drive.task.InitializeTasks
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

/**
 * 应用启动事件
 *
 * @author AkagiYui
 */
@Component
class AfterInitRunner @Autowired constructor(
    private val settingService: SettingService,
    private val initializeTasks: InitializeTasks,
) : ApplicationRunner {
    private val log by LoggerDelegate()

    override fun run(args: ApplicationArguments?) {
        log.debug("====================== ApplicationStarted ======================")
        // 初始化检查
        if (!settingService.initialized) {
            initializeTasks.preCheck()
            initializeTasks.initConfig()
            initializeTasks.addRoleAndUser()
            settingService.initialized = true
            log.info("Initialize success")
        }
        log.debug("==================== ApplicationInitialized ====================")
    }
}
