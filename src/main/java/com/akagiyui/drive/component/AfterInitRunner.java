package com.akagiyui.drive.component;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 应用启动事件
 * @author AkagiYui
 */
@Component
public class AfterInitRunner implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) {
        System.out.println("====================== ApplicationRun ======================");
    }
}
