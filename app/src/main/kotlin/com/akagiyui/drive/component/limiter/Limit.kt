package com.akagiyui.drive.component.limiter

import java.util.concurrent.TimeUnit


/**
 * 限流注解
 * <p>
 * 注解会在class字节码文件中存在，在运行时可以通过反射获取到
 * <p>
 * 注解目标：方法
 * <p>
 * 注解生命周期：运行时
 * @author AkagiYui
 */
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Limit(
    /**
     * 资源的key，唯一
     * 作用：不同的接口，不同的流量控制
     */
    val key: String = "",

    /**
     * 每秒放入令牌桶的token数量
     */
    val permitsPerSecond: Long,

    /**
     * 获取令牌最大等待时间
     */
    val timeout: Long = 0,

    /**
     * 等待时间单位(例:分钟/秒/毫秒) 默认:毫秒
     */
    val timeunit: TimeUnit = TimeUnit.MILLISECONDS,
)
