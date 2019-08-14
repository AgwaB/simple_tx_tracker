package com.simple.tracker.config;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.simple.tracker.config.AsyncConfig.TASK_SAMPLE_MAX_POOL_SIZE;
import static com.simple.tracker.config.AsyncConfig.TASK_SAMPLE_QUEUE_CAPACITY;

@Component
public class AsyncManager {

    @Resource(name = "txAsyncExecutor")
    ThreadPoolTaskExecutor txAsyncExecutor;



    public synchronized boolean isBtcAvailable() {
        return txAsyncExecutor.getThreadPoolExecutor().getQueue().size() <= (TASK_SAMPLE_QUEUE_CAPACITY - TASK_SAMPLE_MAX_POOL_SIZE);
    }
}
