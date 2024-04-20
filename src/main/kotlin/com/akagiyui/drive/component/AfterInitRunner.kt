package com.akagiyui.drive.component

import com.akagiyui.common.delegate.LoggerDelegate
import com.akagiyui.drive.service.ConfigService
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
    private val configService: ConfigService,
    private val initializeTasks: InitializeTasks,
) : ApplicationRunner {
    private val log by LoggerDelegate()

    override fun run(args: ApplicationArguments?) {
        log.debug("====================== ApplicationStarted ======================")
        // 初始化检查
        if (!configService.isInitialized()) {
            initializeTasks.preCheck()
            initializeTasks.initConfig()
            initializeTasks.addRoleAndUser()
            configService.setInitialized(true)
            log.info("Initialize success")
        }
        log.debug("==================== ApplicationInitialized ====================")
    }
}
