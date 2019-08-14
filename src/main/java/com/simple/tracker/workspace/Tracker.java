package com.simple.tracker.workspace;

import com.simple.tracker.config.InitConfig;
import io.reactivex.disposables.Disposable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.response.Callback;
import org.web3j.tx.response.QueuingTransactionReceiptProcessor;
import org.web3j.tx.response.TransactionReceiptProcessor;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.web3j.tx.TransactionManager.DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH;
import static org.web3j.tx.TransactionManager.DEFAULT_POLLING_FREQUENCY;

@Component
public class Tracker {
    @Autowired
    private Web3j web3j;
    @Autowired
    private InitConfig initConfig;

    /**
     *     Note: filters are not supported on Infura.
     */
//    @Async("txAsyncExecutor")
    public void run() {
//        Disposable subscription = web3j.transactionFlowable().subscribe(tx -> {
//            if (tx.getFrom().equals(initConfig.getAddress()) || tx.getTo().equals(initConfig.getAddress())) {
//                processTrace(tx);
//            } else {
//                System.out.println(tx.getHash());
//            }
//        });
   }

    private void processTrace(Transaction tx) {
        System.out.println("---------------------------------------------");
        System.out.println("Tx : " + tx.getHash() + " is confirmed");
        System.out.println("---------------------------------------------");
    }
}
