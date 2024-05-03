package com.akagiyui.drive.config

import com.akagiyui.common.token.TokenTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Token 配置类
 * @author AkagiYui
 */
@Configuration
class TokenConfig {
    @Bean
    fun tokenTemplate(
        @Value("\${application.token.key}") secretKey: String,
        @Value("\${application.token.timeout}") duration: Int,
    ): TokenTemplate {
        return TokenTemplate(secretKey, duration)
    }
}
