package com.simple.tracker.app;

import com.simple.tracker.app.util.Web3jUtil;
import com.simple.tracker.app.value.TxLog;
import com.simple.tracker.app.value.TxStatus;
import com.simple.tracker.config.InitConfig;
import io.reactivex.disposables.Disposable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static final BigInteger GAS_PRICE = BigInteger.valueOf(22_000_000_000L);
    private static final BigInteger GAS_LIMIT = BigInteger.valueOf(4_300_000);

    @Autowired
    private Admin web3j;
    @Autowired
    private Web3jUtil web3jUtil;
    @Autowired
    private InitConfig initConfig;

    @Async("txAsyncExecutor")
    public void sendTxWithNonce(Credentials from, BigInteger nonce, String to, long value) throws Exception {
//        BigInteger nonce = getNonce(from.getAddress());
        BigInteger wei = Convert.toWei(Long.toString(value), Convert.Unit.ETHER).toBigInteger();
        RawTransaction rawTransaction = RawTransaction.createEtherTransaction(
                nonce,
                GAS_PRICE,
                GAS_LIMIT,
                to,
                wei
        );

        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, from);
        String hexValue = Numeric.toHexString(signedMessage);

        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
        String transactionHash = ethSendTransaction.getTransactionHash();
        logger.info("\n[TX]"
                + "\nhash : " + transactionHash
                + "\nstatus : " + TxStatus.PENDING
        );

        TransactionReceipt transactionReceipt = web3jUtil.waitForTransactionReceipt(transactionHash);
        logger.info("\n[TX]"
                + "\nhash : " + transactionReceipt.getTransactionHash()
                + "\nstatus : " + TxStatus.SUCCESS
        );
    }

    @Async("txAsyncExecutor")
    public void sendTxWithoutNonce(Credentials from, String to, long value) throws Exception {
        FastRawTransactionManager transactionManager = new FastRawTransactionManager(
                web3j,
                from,
                getQueuingTransactionReceiptProcessor()
        );

        Transfer transfer = new Transfer(web3j, transactionManager);

        BigInteger gasPrice = transfer.requestCurrentGasPrice();
        TransactionReceipt transactionReceipt = createTransaction(transfer, to, value, gasPrice).send();
        logger.info("\n[TX]"
                + "\nhash : " + transactionReceipt.getTransactionHash()
                + "\nstatus : " + TxStatus.PENDING
        );
        // now maybe pending
    }

    private QueuingTransactionReceiptProcessor getQueuingTransactionReceiptProcessor() {
        return new QueuingTransactionReceiptProcessor(
                web3j,
                new Callback() {
                    @Override
                    public void accept(TransactionReceipt transactionReceipt) {
                        logger.info("\n[TX]"
                                + "\nhash : " + transactionReceipt.getTransactionHash()
                                + "\nstatus : " + TxStatus.SUCCESS
                        );
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

    boolean unlockAccount(String from, String password) throws Exception {
        PersonalUnlockAccount personalUnlockAccount =
                web3j.personalUnlockAccount(
                        from, "coral sun animal ensure remember stadium endorse mechanic draft crawl retire civil", ACCOUNT_UNLOCK_DURATION)
                        .sendAsync()
                        .get();
        return personalUnlockAccount.accountUnlocked();
    }
}
