package com.simple.tracker.config;

import com.simple.tracker.app.TxRequest;
import com.simple.tracker.app.UnconfirmedTx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
public class QueueConfig {
    @Autowired
    private InitConfig initConfig;

    @Bean(name = "requestTxQueue")
    public BlockingQueue<TxRequest> requestTxQueue() {
        return new LinkedBlockingQueue(initConfig.getTxQueueSize());
    }

    @Bean(name = "unconfirmedTxQueue")
    public BlockingQueue<UnconfirmedTx> unconfirmedTxQueue() {
        return new LinkedBlockingQueue(initConfig.getTxQueueSize());
    }
}
