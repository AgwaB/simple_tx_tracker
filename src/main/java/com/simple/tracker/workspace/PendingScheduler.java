package com.simple.tracker.workspace;

import com.simple.tracker.app.value.PendingTx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;

@Component
public class PendingScheduler {
    private static final int BREAK_TIME = 15 * 1000;
    @Autowired
    @Qualifier("pendingQueue")
    private BlockingQueue<PendingTx> pendingQueue;

    @Scheduled(fixedDelay = BREAK_TIME)
    public void run() {
        for (PendingTx pendingTx : pendingQueue) {
            // pending process
        }
    }
}
