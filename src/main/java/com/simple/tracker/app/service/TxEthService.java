package com.simple.tracker.app.service;

import com.simple.tracker.Web3jProvider.NonceTransfer;
import com.simple.tracker.Web3jProvider.RawTransactionManager;
import com.simple.tracker.app.util.Web3jUtil;
import com.simple.tracker.app.value.TxLog;
import com.simple.tracker.app.value.TxStatus;
import com.simple.tracker.config.InitConfig;
import com.simple.tracker.domain.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.FastRawTransactionManager;
import org.web3j.tx.Transfer;
import org.web3j.tx.response.QueuingTransactionReceiptProcessor;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;

@Service
public class TxEthService {
    Logger logger = LoggerFactory.getLogger("txLogger");

    @Autowired
    private Admin web3j;
    @Autowired
    private TxService txService;
    @Autowired
    @Qualifier("queuingTxReceipt")
    private QueuingTransactionReceiptProcessor queuingTransactionReceiptProcessor;

    // TODO : exception chaing to Scheduler.class ?
    @Async("txAsyncExecutor")
    public synchronized void sendTxWithNonce(Credentials from, BigInteger nonce, String to, String gasPrice, String gasLimit, long value) throws Exception {
        RawTransactionManager rawTransactionManager =
                new RawTransactionManager(
                        web3j,
                        from,
                        nonce,
                        queuingTransactionReceiptProcessor
                );

        NonceTransfer nonceTransfer = new NonceTransfer(web3j, rawTransactionManager);

        TransactionReceipt transactionReceipt = createNonceTransaction(
                nonceTransfer,
                to,
                value,
                new BigInteger(gasPrice),
                new BigInteger(gasLimit)
        ).send();

        txService.processTx(
                Transaction.builder()
                        .txId(transactionReceipt.getTransactionHash())
                        .isContract(false)
                        .txStatus(TxStatus.PENDING)
                        .from(from.getAddress())
                        .to(to)
                        .value(BigInteger.valueOf(value))
                        .build()
        );
    }

    @Async("txAsyncExecutor")
    public synchronized void sendTxWithoutNonce(Credentials from, String to, String gasPrice, String gasLimit, long value) throws Exception {
        FastRawTransactionManager transactionManager = new FastRawTransactionManager(
                web3j,
                from,
                queuingTransactionReceiptProcessor
        );

        Transfer transfer = new Transfer(web3j, transactionManager);

        TransactionReceipt transactionReceipt = createTransaction(
                transfer,
                to,
                value,
                new BigInteger(gasPrice),
                new BigInteger(gasLimit)
        ).send();

        txService.processTx(
                Transaction.builder()
                        .txId(transactionReceipt.getTransactionHash())
                        .isContract(false)
                        .txStatus(TxStatus.PENDING)
                        .from(from.getAddress())
                        .to(to)
                        .value(BigInteger.valueOf(value))
                        .build()
        );

        // now maybe pending
    }

    private RemoteCall<TransactionReceipt> createTransaction(
            Transfer transfer, String to, long value, BigInteger gasPrice, BigInteger gasLimit) {
        return transfer.sendFunds(
                to,
                BigDecimal.valueOf(value),
                Convert.Unit.WEI,
                gasPrice,
                gasLimit);
    }

    private RemoteCall<TransactionReceipt> createNonceTransaction(
            NonceTransfer nonceTransfer, String to, long value, BigInteger gasPrice, BigInteger gasLimit) {
        return nonceTransfer.sendFunds(
                to,
                BigDecimal.valueOf(value),
                Convert.Unit.WEI,
                gasPrice,
                gasLimit);
    }

    private void processTx() {

    }
}
