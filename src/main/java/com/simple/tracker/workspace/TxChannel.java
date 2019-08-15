package com.simple.tracker.workspace;

import com.simple.tracker.app.util.Web3jUtil;
import com.simple.tracker.app.value.TxLog;
import com.simple.tracker.config.InitConfig;
import io.reactivex.disposables.Disposable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import static com.simple.tracker.app.value.Web3jDefaultValue.DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH;
import static com.simple.tracker.app.value.Web3jDefaultValue.POLLING_FREQUENCY;

@Component
public class TxChannel {

    @Autowired
    private Admin web3j;
    @Autowired
    private Web3jUtil web3jUtil;
    @Autowired
    private InitConfig initConfig;

    /**
     *     Note: filters are not supported on Infura.
     */
    @Async("txAsyncExecutor")
    public void txSocket() {
        Disposable subscription = web3j.transactionFlowable().subscribe(tx -> {
            if (tx.getFrom().equals(initConfig.getAddress()) || tx.getTo().equals(initConfig.getAddress())) {
                TransactionReceipt transactionReceipt = web3jUtil.getTransactionReceipt(tx.getHash(), POLLING_FREQUENCY, DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH).get();
                TxLog.info("TRACE",
                        "status", transactionReceipt.getStatus(),
                        "error", transactionReceipt.getLogs().toString()
                        );
            }
        });
    }
}
