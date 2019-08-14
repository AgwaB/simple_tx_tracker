package com.simple.tracker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {
    public static final int DEFALUT_TASK_POOL_SIZE = 1;
    /**
     * 샘플 최대 Thread 수
     */
    public static int TASK_SAMPLE_MAX_POOL_SIZE = DEFALUT_TASK_POOL_SIZE;
    /**
     * 샘플 기본 Thread 수
     */
    public static int TASK_SAMPLE_CORE_POOL_SIZE = TASK_SAMPLE_MAX_POOL_SIZE;
    /**
     * 샘플 QUEUE 수
     */
    public static int TASK_SAMPLE_QUEUE_CAPACITY = TASK_SAMPLE_MAX_POOL_SIZE * TASK_SAMPLE_MAX_POOL_SIZE;


    @Value("${tx.threadpool}")
    private int txThreadPoolSize;

    @PostConstruct
    public void prepare() {
        TASK_SAMPLE_MAX_POOL_SIZE = txThreadPoolSize;
        TASK_SAMPLE_CORE_POOL_SIZE = TASK_SAMPLE_MAX_POOL_SIZE;
        TASK_SAMPLE_QUEUE_CAPACITY = TASK_SAMPLE_CORE_POOL_SIZE * TASK_SAMPLE_MAX_POOL_SIZE;
    }

    @Bean(name = "txAsyncExecutor")
    public Executor getAsyncTxExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(TASK_SAMPLE_CORE_POOL_SIZE);
        executor.setMaxPoolSize(TASK_SAMPLE_MAX_POOL_SIZE);
        executor.setQueueCapacity(TASK_SAMPLE_QUEUE_CAPACITY);
        executor.setBeanName("btc");
        executor.initialize();
        return executor;
    }

}