package com.akagiyui.drive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Kenko Drive 云盘
 * @author AkagiYui
 */
@SpringBootApplication
@EnableAsync
public class KenkoDriveApplication {

    public static void main(String[] args) {
        SpringApplication.run(KenkoDriveApplication.class, args);
    }

}
