package com.simple.tracker.app.service;

import com.simple.tracker.app.UnconfirmedTx;
import com.simple.tracker.app.util.Web3jUtil;
import com.simple.tracker.app.value.TxLog;
import com.simple.tracker.app.value.TxStatus;
import com.simple.tracker.domain.Transaction;
import com.simple.tracker.domain.TransactionRepository;
import com.simple.tracker.domain.error.InvalidValueException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class TxService {
    @Autowired
    private Web3jUtil web3jUtil;
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

    public Transaction findByTxId(String txId) {
        return transactionRepository.findById(txId).orElseThrow(() -> new InvalidValueException("Invalid txId : " + txId));
    }

    public boolean checkCanceled(UnconfirmedTx tx) throws ExecutionException, InterruptedException {
        BigInteger confirmedNonce = web3jUtil.getNonce(tx.getFrom());
        if (tx.getNonce().compareTo(confirmedNonce) <= 0) {
            return true;
        }
        return false;
    }

    // pending if not unknown
    public boolean checkIsUnknown(UnconfirmedTx tx, List<String> pendingList) {
        if(!tx.isFirstChecked()) {
            return false;
        }

        for(String pendingTxId : pendingList) {
            if (pendingTxId.equals(tx.getTxId())) {
                return false;
            }
        }
        return true;
    }

    public boolean checkIsConfirmed(UnconfirmedTx tx) throws Exception {
        if(!web3jUtil.sendTransactionReceiptRequest(tx.getTxId()).isPresent()) {
            return false;
        }
        return true;
    }

    public TxStatus getConfirmedStatus(String txId) throws Exception {
        TransactionReceipt receipt = web3jUtil.sendTransactionReceiptRequest(txId).orElseThrow(() -> new InvalidValueException("Invalid receipt request : " + txId));
        if (receipt.getStatus().equals("0x1")) {
            return TxStatus.SUCCESS;
        }
        return TxStatus.FAIL;
    }

    public boolean checkIsLost(UnconfirmedTx tx, List<String> pendingList) {
        if(pendingList.contains(tx.getTxId())) {
            return false;
        }
        return true;
    }
}
