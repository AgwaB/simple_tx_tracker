package com.simple.tracker.app.service;

import com.simple.tracker.Web3jProvider.RawTransactionManager;
import com.simple.tracker.app.value.EthForm;
import com.simple.tracker.app.value.TxStatus;
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
import org.web3j.tx.TransactionManager;
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
    public synchronized void sendTxWithNonce(Credentials from, EthForm ethForm) throws Exception {
        RawTransactionManager rawTransactionManager =
                new RawTransactionManager(
                        web3j,
                        from,
                        ethForm.getNonce(),
                        queuingTransactionReceiptProcessor
                );

        processSend(rawTransactionManager, from, ethForm);
    }

    @Async("txAsyncExecutor")
    public synchronized void sendTxWithoutNonce(Credentials from, EthForm ethForm) throws Exception {
        FastRawTransactionManager transactionManager = new FastRawTransactionManager(
                web3j,
                from,
                queuingTransactionReceiptProcessor
        );

        processSend(transactionManager, from, ethForm);
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

    private void processSend(TransactionManager transactionManager, Credentials from, EthForm ethForm) throws Exception {
        Transfer transfer = new Transfer(web3j, transactionManager);

        TransactionReceipt transactionReceipt = createTransaction(
                transfer,
                ethForm.getTo(),
                ethForm.getValue(),
                new BigInteger(ethForm.getGasPrice()),
                new BigInteger(ethForm.getGasLimit())
        ).send();

        txService.processTx(
                Transaction.builder()
                        .txId(transactionReceipt.getTransactionHash())
                        .isContract(false)
                        .txStatus(TxStatus.PENDING)
                        .from(from.getAddress())
                        .to(ethForm.getTo())
                        .value(BigInteger.valueOf(ethForm.getValue()))
                        .build()
        );
    }
}
