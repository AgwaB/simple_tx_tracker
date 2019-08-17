package com.simple.tracker.config;

import com.simple.tracker.app.TxRequest;
import com.simple.tracker.app.value.PendingTx;
import com.simple.tracker.app.value.TxLog;
import com.simple.tracker.app.value.TxStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.response.Callback;
import org.web3j.tx.response.QueuingTransactionReceiptProcessor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import static com.simple.tracker.app.value.DefaultValue.DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH;
import static com.simple.tracker.app.value.DefaultValue.POLLING_FREQUENCY;

@Configuration
public class QueueConfig {
    @Autowired
    private InitConfig initConfig;

    @Bean(name = "txQueue")
    public BlockingQueue<TxRequest> txQueue() {
        return new LinkedBlockingQueue(initConfig.getTxQueueSize());
    }

    @Bean(name = "pendingQueue")
    public BlockingQueue<PendingTx> pendingQueue() {
        return new LinkedBlockingQueue(initConfig.getTxQueueSize());
    }
}
