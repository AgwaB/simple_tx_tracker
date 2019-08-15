package com.simple.tracker.workspace;

import com.simple.tracker.app.CredentialsService;
import com.simple.tracker.app.TxEthService;
import com.simple.tracker.app.value.TxForm;
import com.simple.tracker.app.value.TxLog;
import com.simple.tracker.config.AsyncManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.web3j.crypto.Credentials;

import javax.annotation.PostConstruct;
import java.io.IOException;
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
    private TxEthService txEthService;
    @Autowired
    private CredentialsService credentialsService;
    @Autowired
    private TxChannel txChannel;

    @PostConstruct
    public void openChannel() {
        txChannel.txSocket();
    }

    @Scheduled(fixedDelay = 10)
    public void run() {
        try {
            if (asyncManager.isBtcAvailable()) {
                TxForm txForm = (TxForm) txQueue.take();
                Credentials credentials = credentialsService.getCredentialsByPrivKey(txForm.getFromPrivKey());
                txEthService.sendTxWithoutNonce(credentials, txForm.getTo(), txForm.getValue());
                TxLog.info("TX REQUEST",
                        "from", credentials.getAddress(),
                        "to", txForm.getTo(),
                        "value", Long.toString(txForm.getValue())
                        );
            } else {
                Thread.sleep(BREAK_TIME);
            }
        } catch (InterruptedException e) {
            // thread.sleep
        } catch (IOException e) {
            // requestCurrentGasPrice
        } catch (Exception e) {
            // send exception
        }
    }
}
