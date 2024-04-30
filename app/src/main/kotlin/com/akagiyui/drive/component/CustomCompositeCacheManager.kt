package com.akagiyui.drive.component

import io.lettuce.core.event.Event
import io.lettuce.core.event.connection.ConnectionActivatedEvent
import io.lettuce.core.event.connection.ConnectionDeactivatedEvent
import io.lettuce.core.resource.ClientResources
import jakarta.annotation.Nullable
import jakarta.annotation.PostConstruct
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.cache.support.NoOpCacheManager
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.stereotype.Component
import java.util.*

/**
 * 自定义二级缓存管理器
 *
 * @author AkagiYui
 */
@Component
@Primary
class CustomCompositeCacheManager(
    /**
     * 本地缓存管理器
     */
    private val caffeineCacheManager: CaffeineCacheManager,
    /**
     * Redis缓存管理器
     */
    private val redisCacheManager: RedisCacheManager,
    /**
     * Redis连接工厂
     */
    private val lettuceConnectionFactory: LettuceConnectionFactory,
) : CacheManager {
    /**
     * 空缓存管理器
     */
    private val noOpCacheManager = NoOpCacheManager()
    private var isRedisOnline = false

    @Nullable
    override fun getCache(name: String): Cache? {
        // todo 这里会一直返回本地缓存，不返回Redis缓存，暂时只使用 Redis 缓存
//        Cache cache = caffeineCacheManager.getCache(name);
//        if (cache != null) {
//            return cache;
//        }
        if (isRedisOnline) {
            return redisCacheManager.getCache(name)
        }
        return noOpCacheManager.getCache(name) // 没有缓存，返回一个空的缓存，防止空指针
    }

    override fun getCacheNames(): Collection<String> {
        val names: MutableSet<String> = LinkedHashSet(caffeineCacheManager.cacheNames)
        if (isRedisOnline) {
            names.addAll(redisCacheManager.cacheNames)
        }
        return Collections.unmodifiableSet(names)
    }

    /**
     * 注册Redis连接事件监听器
     */
    @PostConstruct
    fun registerListener() {
        val clientConfiguration = lettuceConnectionFactory.clientConfiguration

        // 如果已连接，激活Redis缓存管理器
        if (!lettuceConnectionFactory.connection.isClosed) {
            isRedisOnline = true
        }

        val clientResources = clientConfiguration.clientResources
        clientResources.ifPresent { resources: ClientResources ->
            resources.eventBus().get().subscribe { e: Event? ->
                if (e is ConnectionDeactivatedEvent) {
                    // 取消使用Redis缓存
                    log.warn("Redis is offline, deactivate RedisCacheManager")
                    isRedisOnline = false
                } else if (e is ConnectionActivatedEvent) {
                    // 恢复使用Redis缓存管理器
                    log.debug("Redis is online, activate RedisCacheManager")
                    isRedisOnline = true
                }
            }
        }
    }
}
