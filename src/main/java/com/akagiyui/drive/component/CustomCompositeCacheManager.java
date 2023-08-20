package com.akagiyui.drive.component;

import io.lettuce.core.event.connection.ConnectionActivatedEvent;
import io.lettuce.core.event.connection.ConnectionDeactivatedEvent;
import io.lettuce.core.resource.ClientResources;
import jakarta.annotation.Nullable;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 自定义二级缓存管理器
 *
 * @author AkagiYui
 */
@Slf4j
@Component
@Primary
public class CustomCompositeCacheManager implements CacheManager {

    /**
     * 本地缓存管理器
     */
    @Resource
    private CaffeineCacheManager caffeineCacheManager;

    /**
     * Redis缓存管理器
     */
    @Resource
    private RedisCacheManager redisCacheManager;

    /**
     * Redis连接工厂
     */
    @Resource
    private LettuceConnectionFactory lettuceConnectionFactory;

    /**
     * 空缓存管理器
     */
    private final NoOpCacheManager noOpCacheManager = new NoOpCacheManager();

    private boolean isRedisOnline = false;

    @Override
    @Nullable
    public Cache getCache(@NotNull String name) {
        // todo 这里会一直返回本地缓存，不返回Redis缓存，暂时只使用 Redis 缓存
//        Cache cache = caffeineCacheManager.getCache(name);
//        if (cache != null) {
//            return cache;
//        }
        if (isRedisOnline) {
            return redisCacheManager.getCache(name);
        }
        return noOpCacheManager.getCache(name); // 没有缓存，返回一个空的缓存，防止空指针
    }

    @Override
    @NotNull
    public Collection<String> getCacheNames() {
        Set<String> names = new LinkedHashSet<>(caffeineCacheManager.getCacheNames());
        if (isRedisOnline) {
            names.addAll(redisCacheManager.getCacheNames());
        }
        return Collections.unmodifiableSet(names);
    }

    /**
     * 不使用Redis缓存
     */
    public void deactivateRedisCacheManager() {
        log.error("Redis is offline, deactivate RedisCacheManager");
        isRedisOnline = false;
    }

    /**
     * 使用Redis缓存
     */
    public void activateRedisCacheManager() {
        log.debug("Redis is online, activate RedisCacheManager");
        isRedisOnline = true;
    }

    /**
     * 注册Redis连接事件监听器
     */
    @PostConstruct
    public void registerListener() {
        LettuceClientConfiguration clientConfiguration = lettuceConnectionFactory.getClientConfiguration();

        // 如果已连接，激活Redis缓存管理器
        if (!lettuceConnectionFactory.getConnection().isClosed()) {
            activateRedisCacheManager();
        }

        Optional<ClientResources> clientResources = clientConfiguration.getClientResources();
        clientResources.ifPresent(resources -> resources.eventBus().get().subscribe(e -> {
            if (e instanceof ConnectionDeactivatedEvent) {
                deactivateRedisCacheManager();
            } else if (e instanceof ConnectionActivatedEvent) {
                activateRedisCacheManager();
            }
        }));
    }
}
