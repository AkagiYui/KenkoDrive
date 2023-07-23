package com.akagiyui.drive.component.limiter;

import com.akagiyui.drive.exception.TooManyRequestsException;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 限流 AOP
 * @author AkagiYui
 */
@SuppressWarnings("UnstableApiUsage")
@Aspect
@Slf4j
@Component
public class LimitAspect {
    /**
     * 限流器字典
     * map的key为 Limit.key
     */
    private final Map<String, RateLimiter> limitMap = Maps.newConcurrentMap();

    @Around("@annotation(com.akagiyui.drive.component.limiter.Limit)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        // 获取 Limit 注解
        Limit limit = method.getAnnotation(Limit.class);
        if (limit != null) {
            String key = limit.key();
            RateLimiter rateLimiter;
            // 验证缓存是否有命中 key
            if (!limitMap.containsKey(key)) {
                // 创建令牌桶
                rateLimiter = RateLimiter.create(limit.permitsPerSecond());
                limitMap.put(key, rateLimiter);
                log.debug("Created RateLimiter: {}", rateLimiter);
            } else {
                rateLimiter = limitMap.get(key);
            }
            // 获取令牌
            boolean acquire = rateLimiter.tryAcquire(limit.timeout(), limit.timeunit());
            // 拿不到令牌，抛出异常
            if (!acquire) {
                log.warn("Too many requests: {}", key);
                throw new TooManyRequestsException();
            }
        }
        return joinPoint.proceed();
    }
}
