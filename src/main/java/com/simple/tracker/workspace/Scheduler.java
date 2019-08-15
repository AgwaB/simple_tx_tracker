package com.simple.tracker.workspace;

import com.simple.tracker.app.CredentialsService;
import com.simple.tracker.app.TransactionService;
import com.simple.tracker.app.value.TxForm;
import com.simple.tracker.config.AsyncManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.web3j.crypto.Credentials;

import java.util.concurrent.BlockingQueue;

@Component
public class Scheduler {
    Logger logger = LoggerFactory.getLogger("txLogger");
    private static final int BREAK_TIME = 5 * 1000;
    @Autowired
    @Qualifier("linkedBlockingQueue")
    private BlockingQueue txQueue;
    @Autowired
    private AsyncManager asyncManager;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private CredentialsService credentialsService;

    @Scheduled(fixedDelay = 10)
    public void run() {
        try {
            if (asyncManager.isBtcAvailable()) {
                TxForm txForm = (TxForm) txQueue.take();
                Credentials credentials = credentialsService.getCredentialsByPrivKey(txForm.getFromPrivKey());
                transactionService.sendTx(credentials, txForm.getTo(), txForm.getValue());
                logger.info("\n[TX REQUEST]"
                        + "\nfrom : " + credentials.getAddress()
                        + "\nto : " + txForm.getTo()
                        + "\nvalue : " + txForm.getValue()
                );
            } else {
                Thread.sleep(BREAK_TIME);
            }
        } catch (InterruptedException e) {

        }
    }
}
