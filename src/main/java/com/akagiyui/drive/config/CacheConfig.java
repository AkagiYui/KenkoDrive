package com.akagiyui.drive.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;
import java.util.Arrays;

/**
 * 缓存配置类
 * @author kenko
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Caffeine本地缓存管理器
     * @return 缓存管理器
     */
    @Bean
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(10))
                .maximumSize(10_000));
        return cacheManager;
    }

    /**
     *  Redis缓存管理器
     * @param redisConnectionFactory Redis连接工厂
     * @return 缓存管理器
     */
    @Bean
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30));
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(redisCacheConfiguration)
                .build();
    }

    /**
     * 组合缓存管理器
     * @param caffeineCacheManager Caffeine缓存管理器
     * @param redisCacheManager Redis缓存管理器
     * @return 缓存管理器
     */
    @Bean
    @Primary
    public CacheManager compositeCacheManager(CacheManager caffeineCacheManager, CacheManager redisCacheManager) {
        CompositeCacheManager cacheManager = new CompositeCacheManager();
        cacheManager.setCacheManagers(Arrays.asList(redisCacheManager, caffeineCacheManager));
        return cacheManager;
    }

}
