package com.simple.tracker.app.service;

import com.simple.tracker.app.value.TxLog;
import com.simple.tracker.app.value.TxStatus;
import com.simple.tracker.domain.Transaction;
import com.simple.tracker.domain.TransactionRepository;
import com.simple.tracker.domain.error.InvalidValueException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TxService {
    @Autowired
    private TransactionRepository transactionRepository;

    public void changeTxStatus(String txId, TxStatus txStatus) {
        Transaction tx = transactionRepository.findById(txId).orElseThrow(() -> new InvalidValueException("Invalid txId : " + txId));
        tx.changeStatus(txStatus);
        transactionRepository.save(tx);
        TxLog.info("TX",
                "hash", tx.getTxId(),
                "status", tx.getTxStatus().toString()
        );
    }

    public void processTx(Transaction tx) throws Exception {
        transactionRepository.save(tx);
        TxLog.info("TX",
                "hash", tx.getTxId(),
                "status", tx.getTxStatus().toString()
                );
    }
}
