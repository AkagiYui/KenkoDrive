package com.akagiyui.common.limiter;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

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
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface Limit {
    /**
     * 资源的key，唯一
     * 作用：不同的接口，不同的流量控制
     */
    String key() default "";

    /**
     * 每秒放入令牌桶的token数量
     */
    double permitsPerSecond();

    /**
     * 获取令牌最大等待时间
     */
    long timeout();

    /**
     * 等待时间单位(例:分钟/秒/毫秒) 默认:毫秒
     */
    TimeUnit timeunit() default TimeUnit.MILLISECONDS;
}
