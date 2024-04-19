package com.akagiyui.drive.component;

import com.akagiyui.drive.service.ConfigService;
import com.akagiyui.drive.task.InitializeTasks;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 应用启动事件
 * @author AkagiYui
 */
@Component
@Slf4j
public class AfterInitRunner implements ApplicationRunner {

    private final ConfigService configService;
    private final InitializeTasks initializeTasks;

    public AfterInitRunner(ConfigService configService, InitializeTasks initializeTasks) {
        this.configService = configService;
        this.initializeTasks = initializeTasks;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.debug("====================== ApplicationStarted ======================");
        // 初始化检查
        if (!configService.isInitialized()) {
            initializeTasks.preCheck();
            initializeTasks.initConfig();
            initializeTasks.addRoleAndUser();
            configService.setInitialized(true);
            log.info("Initialize success");
        }
        log.debug("==================== ApplicationInitialized ====================");
    }
}
