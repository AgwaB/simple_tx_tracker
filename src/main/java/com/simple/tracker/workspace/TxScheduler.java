package com.simple.tracker.workspace;

import com.simple.tracker.app.TxRequest;
import com.simple.tracker.app.value.TxLog;
import com.simple.tracker.config.AsyncManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

@Component
public class TxScheduler {
    private static final int BREAK_TIME = 1 * 500;
    @Autowired
    @Qualifier("requestTxQueue")
    private BlockingQueue<TxRequest> txQueue;
    @Autowired
    private AsyncManager asyncManager;

    private int sequence = 0;

    @Scheduled(fixedDelay = 10)
    public void run() {
        try {
            if (asyncManager.isBtcAvailable()) {
                TxRequest txRequest = txQueue.take();

                sequence++;
                TxLog.info("REQUEST",
                        "sequence", Integer.toString(sequence)
                );

                txRequest.execute();
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
