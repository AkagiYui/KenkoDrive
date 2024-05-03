package com.akagiyui.drive

import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

/**
 * Kenko Drive 云盘
 * @author AkagiYui
 */
@SpringBootApplication // Spring Boot 应用
@EnableAsync // 开启异步
@EnableScheduling // 开启定时任务
class KenkoDriveApplication

fun main(args: Array<String>) {
    runApplication<KenkoDriveApplication>(*args) {
        setBannerMode(Banner.Mode.CONSOLE) // 只在控制台输出 Spring Boot 启动图标
    }
}
