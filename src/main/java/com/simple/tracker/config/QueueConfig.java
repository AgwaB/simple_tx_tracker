package com.simple.tracker.config;

import com.simple.tracker.app.value.TxForm;
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

import static com.simple.tracker.app.value.Web3jDefaultValue.DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH;
import static com.simple.tracker.app.value.Web3jDefaultValue.POLLING_FREQUENCY;

@Configuration
public class QueueConfig {
    @Autowired
    private InitConfig initConfig;
    @Autowired
    private Admin web3j;

    @Bean(name = "linkedBlockingQueue")
    public BlockingQueue<TxForm> linkedBlockingQueue() {
        return new LinkedBlockingQueue(initConfig.getTxQueueSize());
    }

    @Bean(name = "priorityBlockingQueue")
    public BlockingQueue<TxForm> priorityBlockingQueue() {
        return new PriorityBlockingQueue();
    }

    @Bean(name = "QueuingTxReceipt")
    public QueuingTransactionReceiptProcessor getQueuingTransactionReceiptProcessor() {
        return new QueuingTransactionReceiptProcessor(
                web3j,
                new Callback() {
                    @Override
                    public void accept(TransactionReceipt transactionReceipt) {
                        if (transactionReceipt.getStatus().equals("0x1")) {
                            TxLog.info(
                                    "TX",
                                    "hash", transactionReceipt.getTransactionHash(),
                                    "status", TxStatus.SUCCESS.toString()
                            );
                        }

                        if (transactionReceipt.getStatus().equals("0x0")) {
                            TxLog.info(
                                    "TX",
                                    "hash", transactionReceipt.getTransactionHash(),
                                    "status", TxStatus.FAIL.toString()
                            );
                        }
                    }

                    @Override
                    public void exception(Exception exception) {
                    }
                },
                DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH,
                POLLING_FREQUENCY);
    }
}
