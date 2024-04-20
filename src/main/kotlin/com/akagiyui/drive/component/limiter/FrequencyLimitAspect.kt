package com.akagiyui.drive.component.limiter

import com.akagiyui.common.delegate.LoggerDelegate
import com.akagiyui.common.exception.TooManyRequestsException
import com.google.common.collect.Maps
import com.google.common.util.concurrent.RateLimiter
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component

/**
 * 限流 AOP
 * @author AkagiYui
 */
@Suppress("UnstableApiUsage")
@Aspect
@Component
class FrequencyLimitAspect {
    private val log by LoggerDelegate()

    /**
     * 限流器字典
     * map的key为 Limit.key
     */
    private val limitMap: MutableMap<String, RateLimiter> = Maps.newConcurrentMap()

    @Around("@annotation(Limit)")
    @Throws(Throwable::class)
    fun around(joinPoint: ProceedingJoinPoint): Any {
        // 获取方法签名
        val signature = joinPoint.signature as MethodSignature
        val method = signature.method
        // 获取 Limit 注解
        val limit = method.getAnnotation(Limit::class.java)
        if (limit != null) {
            val key = limit.key
            // 验证缓存是否有命中 key
            val rateLimiter = if (!limitMap.containsKey(key)) {
                // 创建令牌桶
                val rateLimiter = RateLimiter.create(limit.permitsPerSecond)
                limitMap[key] = rateLimiter
                log.debug("Created RateLimiter: {}", limitMap[key])
                rateLimiter  // 减少一次访问
            } else {
                limitMap[key] as RateLimiter
            }
            // 获取令牌
            val acquire = rateLimiter.tryAcquire(limit.timeout, limit.timeunit)
            // 拿不到令牌，抛出异常
            if (!acquire) {
                log.debug("Too many requests: {}", key)
                throw TooManyRequestsException()
            }
        }
        return joinPoint.proceed()
    }
}
