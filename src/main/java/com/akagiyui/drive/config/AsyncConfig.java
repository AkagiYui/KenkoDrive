package com.akagiyui.drive.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 线程池配置类
 *
 * @author kenko
 */
@EnableAsync
@Configuration
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 常驻的线程数，即使没有任务要执行，线程池一直都会维持这些线程。
        executor.setCorePoolSize(6);
        // 线程池能够容纳的最大线程数。当请求任务数超过核心线程数时，线程池会创建新线程，但是不会超过该值。
        executor.setMaxPoolSize(12);
        // 缓冲提交到线程池但还未被执行的任务。如果线程池的核心线程和最大线程都满了，新提交的任务会进入队列中等待执行。
        executor.setQueueCapacity(100);
        return executor;
    }

}
