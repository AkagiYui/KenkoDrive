package com.akagiyui.drive.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer

/**
 * Redis配置类
 *
 * @author AkagiYui
 */
@Configuration
class RedisConfig {

    /**
     * RedisTemplate配置
     *
     * @param connectionFactory Redis连接工厂
     * @return RedisTemplate
     */
    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, Any?> {
        // 创建RedisTemplate
        return RedisTemplate<String, Any?>().apply {
            setConnectionFactory(connectionFactory)
            setDefaultSerializer(GenericJackson2JsonRedisSerializer())
            // 键使用字符串字面量，否则会在键前后加上双引号
            keySerializer = stringSerializer
        }
    }
}
