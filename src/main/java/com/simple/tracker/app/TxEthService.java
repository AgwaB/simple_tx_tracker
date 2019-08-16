package com.simple.tracker.app;

import com.simple.tracker.app.util.Web3jUtil;
import com.simple.tracker.app.value.TxLog;
import com.simple.tracker.app.value.TxStatus;
import com.simple.tracker.config.InitConfig;
import io.reactivex.disposables.Disposable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.ChainId;
import org.web3j.tx.FastRawTransactionManager;
import org.web3j.tx.Transfer;
import org.web3j.tx.response.Callback;
import org.web3j.tx.response.QueuingTransactionReceiptProcessor;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;

import static com.simple.tracker.app.value.Web3jDefaultValue.*;

@Service
public class TxEthService {
    Logger logger = LoggerFactory.getLogger("txLogger");

    @Autowired
    private Admin web3j;
    @Autowired
    private Web3jUtil web3jUtil;
    @Autowired
    private InitConfig initConfig;
    @Autowired
    @Qualifier("QueuingTxReceipt")
    private QueuingTransactionReceiptProcessor queuingTransactionReceiptProcessor;

    @Async("txAsyncExecutor")
    public void sendTxWithNonce(Credentials from, String to, String gasPrice, String gasLimit, long value) throws Exception {
        BigInteger nonce = web3jUtil.getNonce(from.getAddress());
        BigInteger wei = Convert.toWei(Long.toString(value), Convert.Unit.WEI).toBigInteger();
        RawTransaction rawTransaction = RawTransaction.createEtherTransaction(
                nonce,
                new BigInteger(gasPrice),
                new BigInteger(gasLimit),
                to,
                wei
        );

        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, from);
        String hexValue = Numeric.toHexString(signedMessage);

        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
        String transactionHash = ethSendTransaction.getTransactionHash();
        TxLog.info("TX", "hash", transactionHash, "status", TxStatus.PENDING.toString());

        TransactionReceipt transactionReceipt = web3jUtil.waitForTransactionReceipt(transactionHash);
        if (transactionReceipt.getStatus().equals("0x1")) {
            TxLog.info("TX", "hash", transactionHash, "status", TxStatus.SUCCESS.toString());
        }

        if (transactionReceipt.getStatus().equals("0x0")) {
            TxLog.info("TX", "hash", transactionHash, "status", TxStatus.FAIL.toString());
        }
    }

    public void sendTxWithoutNonce(Credentials from, String to, String gasPrice, String gasLimit, long value) throws Exception {
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
        TxLog.info("TX", "hash", transactionReceipt.getTransactionHash(), "status", TxStatus.PENDING.toString());
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

    boolean unlockAccount(String from, String password) throws Exception {
        PersonalUnlockAccount personalUnlockAccount =
                web3j.personalUnlockAccount(
                        from, "coral sun animal ensure remember stadium endorse mechanic draft crawl retire civil", ACCOUNT_UNLOCK_DURATION)
                        .sendAsync()
                        .get();
        return personalUnlockAccount.accountUnlocked();
    }
}
