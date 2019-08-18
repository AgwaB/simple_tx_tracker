package com.simple.tracker.config;

import com.simple.tracker.app.TxRequest;
import com.simple.tracker.app.tx.PendingTx;
import com.simple.tracker.app.tx.SentTx;
import com.simple.tracker.app.tx.UnconfirmedTx;
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

    @Bean(name = "sentTxQueue")
    public BlockingQueue<SentTx> sentTxQueue() {
        return new LinkedBlockingQueue(initConfig.getTxQueueSize());
    }

    @Bean(name = "pendingTxQueue")
    public BlockingQueue<PendingTx> pendingTxQueue() {
        return new LinkedBlockingQueue(initConfig.getTxQueueSize());
    }
}
