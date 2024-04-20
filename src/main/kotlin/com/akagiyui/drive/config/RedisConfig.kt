package com.akagiyui.drive.config

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericToStringSerializer
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer

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
    fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<Any, Any> {
        // 使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值
        val om = ObjectMapper().apply {
            setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
            activateDefaultTyping(polymorphicTypeValidator, ObjectMapper.DefaultTyping.NON_FINAL)
        }
        val jackson2JsonRedisSerializer = Jackson2JsonRedisSerializer(om, Any::class.java)

        // 创建RedisTemplate
        return RedisTemplate<Any, Any>().apply {
            setConnectionFactory(connectionFactory)

            // key采用String序列化方式
            keySerializer = GenericToStringSerializer(Any::class.java)
            valueSerializer = jackson2JsonRedisSerializer

            // Hash的key也采用String的序列化方式
            hashKeySerializer = GenericToStringSerializer(Any::class.java)
            hashValueSerializer = jackson2JsonRedisSerializer

            setEnableTransactionSupport(true)
            afterPropertiesSet()
        }
    }
}
