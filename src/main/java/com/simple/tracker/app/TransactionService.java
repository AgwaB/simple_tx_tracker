package com.simple.tracker.app;

import com.simple.tracker.config.InitConfig;
import io.reactivex.disposables.Disposable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.FastRawTransactionManager;
import org.web3j.tx.Transfer;
import org.web3j.tx.response.Callback;
import org.web3j.tx.response.QueuingTransactionReceiptProcessor;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.web3j.tx.TransactionManager.DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH;

@Service
public class TransactionService {
    public static final int DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH = 40;
    private static final long POLLING_FREQUENCY = 15 * 1000;

    @Autowired
    private Web3j web3j;
    @Autowired
    private InitConfig initConfig;

    @Async("txAsyncExecutor")
    public void sendTx(Credentials from, String to, long value) {
        FastRawTransactionManager transactionManager = new FastRawTransactionManager(
                web3j,
                from,
                getQueuingTransactionReceiptProcessor()
        );

        Transfer transfer = new Transfer(web3j, transactionManager);

        try {
            BigInteger gasPrice = transfer.requestCurrentGasPrice();
            TransactionReceipt transactionReceipt = createTransaction(transfer, to, value, gasPrice).send();
            System.out.println("Tx : " + transactionReceipt.getTransactionHash() + " is now pended");
            // now maybe pending
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private QueuingTransactionReceiptProcessor getQueuingTransactionReceiptProcessor() {
        return new QueuingTransactionReceiptProcessor(
                web3j,
                new Callback() {
                    @Override
                    public void accept(TransactionReceipt transactionReceipt) {
                        System.out.println("Tx : " + transactionReceipt.getTransactionHash() + " is now accepted, status is " + transactionReceipt.getStatus());
                    }

                    @Override
                    public void exception(Exception exception) {
                    }
                },
                DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH,
                POLLING_FREQUENCY);
    }

    private RemoteCall<TransactionReceipt> createTransaction(
            Transfer transfer, String to, long value, BigInteger gasPrice) {
        return transfer.sendFunds(
                to,
                BigDecimal.valueOf(value),
                Convert.Unit.WEI,
                gasPrice,
                Transfer.GAS_LIMIT);
    }

    /**
     *     Note: filters are not supported on Infura.
     */
    @Async("txAsyncExecutor")
    public void trace() {
        Disposable subscription = web3j.transactionFlowable().subscribe(tx -> {
            if (tx.getFrom().equals(initConfig.getAddress()) || tx.getTo().equals(initConfig.getAddress())) {
                // something
            }
        });
    }
}
