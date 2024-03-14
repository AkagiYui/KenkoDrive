package com.akagiyui.drive.component;

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
    @Override
    public void run(ApplicationArguments args) {
        log.debug("====================== ApplicationStarted ======================");
    }
}
