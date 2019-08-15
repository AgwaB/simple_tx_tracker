package com.simple.tracker.app.util;

import com.simple.tracker.app.value.TxLog;
import com.simple.tracker.app.value.TxStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.response.Callback;
import org.web3j.tx.response.QueuingTransactionReceiptProcessor;

import java.math.BigInteger;
import java.util.Optional;

import static com.simple.tracker.app.value.Web3jDefaultValue.DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH;
import static com.simple.tracker.app.value.Web3jDefaultValue.POLLING_FREQUENCY;

@Component
public class Web3jUtil {
    Logger logger = LoggerFactory.getLogger("txLogger");

    @Autowired
    private Admin web3j;

    public TransactionReceipt waitForTransactionReceipt(String transactionHash) throws Exception {

        Optional<TransactionReceipt> transactionReceiptOptional =
                getTransactionReceipt(transactionHash, POLLING_FREQUENCY, DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH);

        if (!transactionReceiptOptional.isPresent()) {
            logger.info("Transaction receipt not generated after " + DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH + " attempts");
        }

        return transactionReceiptOptional.get();
    }

    public Optional<TransactionReceipt> getTransactionReceipt(
            String transactionHash, int sleepDuration, int attempts) throws Exception {

        Optional<TransactionReceipt> receiptOptional =
                sendTransactionReceiptRequest(transactionHash);
        for (int i = 0; i < attempts; i++) {
            if (!receiptOptional.isPresent()) {
                Thread.sleep(sleepDuration);
                receiptOptional = sendTransactionReceiptRequest(transactionHash);
            } else {
                break;
            }
        }

        return receiptOptional;
    }

    public BigInteger getNonce(String address) throws Exception {
        EthGetTransactionCount ethGetTransactionCount =
                web3j.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST)
                        .sendAsync()
                        .get();

        return ethGetTransactionCount.getTransactionCount();
    }

    private Optional<TransactionReceipt> sendTransactionReceiptRequest(String transactionHash)
            throws Exception {
        EthGetTransactionReceipt transactionReceipt =
                web3j.ethGetTransactionReceipt(transactionHash).sendAsync().get();

        return transactionReceipt.getTransactionReceipt();
    }
}
