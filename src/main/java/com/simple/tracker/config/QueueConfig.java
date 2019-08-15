package com.simple.tracker.config;

import com.simple.tracker.app.value.TxForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

@Configuration
public class QueueConfig {
    @Autowired
    private InitConfig initConfig;

    @Bean(name = "linkedBlockingQueue")
    public BlockingQueue<TxForm> linkedBlockingQueue() {
        return new LinkedBlockingQueue(initConfig.getTxQueueSize());
    }

    @Bean(name = "priorityBlockingQueue")
    public BlockingQueue<TxForm> priorityBlockingQueue() {
        return new PriorityBlockingQueue();
    }
}
