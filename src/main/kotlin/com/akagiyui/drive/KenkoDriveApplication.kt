package com.akagiyui.drive

import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync

/**
 * Kenko Drive 云盘
 * @author AkagiYui
 */
@SpringBootApplication // Spring Boot 应用
@EnableAsync // 开启异步
class KenkoDriveApplication

fun main(args: Array<String>) {
    runApplication<KenkoDriveApplication>(*args) {
        setBannerMode(Banner.Mode.OFF) // 关闭 Spring Boot 启动图标
    }
}
