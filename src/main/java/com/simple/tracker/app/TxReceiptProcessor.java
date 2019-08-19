package com.simple.tracker.app;

import com.simple.tracker.app.service.TxService;
import com.simple.tracker.app.value.TxLog;
import com.simple.tracker.app.value.TxStatus;
import com.simple.tracker.domain.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.tx.response.Callback;
import org.web3j.tx.response.QueuingTransactionReceiptProcessor;

import java.util.concurrent.BlockingQueue;

import static com.simple.tracker.app.value.DefaultValue.DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH;
import static com.simple.tracker.app.value.DefaultValue.POLLING_FREQUENCY;

@Component
public class TxReceiptProcessor {
    @Autowired
    private Admin web3j;
    @Autowired
    private TxService txService;
    @Autowired
    @Qualifier("unconfirmedTxQueue")
    private BlockingQueue<UnconfirmedTx> unconfirmedTxQueue;

    @Bean(name = "queuingTxReceipt")
    public QueuingTransactionReceiptProcessor getQueuingTransactionReceiptProcessor() {
        return new QueuingTransactionReceiptProcessor(
                web3j,
                new Callback() {
                    @Override
                    public void accept(TransactionReceipt transactionReceipt) {
                        if (transactionReceipt.getStatus().equals("0x1")) {
                            txService.changeTxStatus(
                                    transactionReceipt.getTransactionHash(),
                                    TxStatus.SUCCESS
                            );
                        }

                        if (transactionReceipt.getStatus().equals("0x0")) {
                            txService.changeTxStatus(
                                    transactionReceipt.getTransactionHash(),
                                    TxStatus.FAIL
                            );
                        }
                    }

                    @Override
                    public void exception(Exception exception) {
                        try {
                            throw exception;
                        } catch (TransactionException e) {
                            TxLog.error(exception.getMessage(), "send to pending transaction pool");
                            Transaction tx = txService.findByTxId(e.getTransactionHash().get());
                            unconfirmedTxQueue.add(
                                    new UnconfirmedTx(tx.getTxId(), tx.getFrom(), tx.getNonce(), TxStatus.PENDING)
                            );
                            return;
                        } catch (Exception e) {
                            TxLog.error(e.getMessage());
                        }
                    }
                },
                DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH,
                POLLING_FREQUENCY);
    }
}
