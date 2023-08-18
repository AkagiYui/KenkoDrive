package com.akagiyui.drive.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 线程池配置类
 *
 * @author kenko
 */
@Configuration
@Slf4j
public class AsyncConfig implements AsyncConfigurer, WebMvcConfigurer {

/**
     * 配置 Spring 异步任务执行器
     */
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

    /**
     * 配置 Web 异步任务执行器，主要用于文件下载
     */
    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setTaskExecutor(new ConcurrentTaskExecutor(Executors.newCachedThreadPool()));
    }

    /**
     * Web 异步任务中发生异常
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, objects) -> log.error("Exception caught in async method", throwable);
    }
}
