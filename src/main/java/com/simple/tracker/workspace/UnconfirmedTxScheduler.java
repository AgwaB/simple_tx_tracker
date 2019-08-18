package com.simple.tracker.workspace;

import com.simple.tracker.app.service.TxService;
import com.simple.tracker.app.tx.PendingTx;
import com.simple.tracker.app.tx.SentTx;
import com.simple.tracker.app.tx.UnconfirmedTx;
import com.simple.tracker.app.util.Web3jUtil;
import com.simple.tracker.app.value.TxLog;
import com.simple.tracker.app.value.TxStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;

@Component
public class UnconfirmedTxScheduler {
    private static final int SENT_BREAK_TIME = 15 * 1000;
    private static final int PENDING_BREAK_TIME = 15 * 1000;
    @Autowired
    @Qualifier("sentTxQueue")
    private BlockingQueue<SentTx> sentTxQueue;
    @Autowired
    @Qualifier("pendingTxQueue")
    private BlockingQueue<PendingTx> pendingTxQueue;
    @Autowired
    private Web3jUtil web3jUtil;
    @Autowired
    private TxService txService;

    @Scheduled(fixedDelay = SENT_BREAK_TIME)
    public void runSentSchedule() {
        for (SentTx sentTx : sentTxQueue) {
            try {
                if(checkCanceled(sentTx)) {
                    txService.changeTxStatus(sentTx.getTxId(), TxStatus.SENT_CANCEL);
                    sentTxQueue.remove(sentTx);
                    continue;
                }
                pendingTxQueue.add(new PendingTx(sentTx.getTxId()));
            } catch (ExecutionException e) {
                TxLog.error(e.getMessage(), "");
                // get nonce
            } catch (InterruptedException e) {
                // get nonce
            }
        }
    }

    @Scheduled(fixedDelay = PENDING_BREAK_TIME)
    public void runPendingSchedule() {
        /**
         * pendingTxList = getPendingTxList;
         *
          */
        for (PendingTx pendingTx : pendingTxQueue) {
            /**
             * if (pendingTx.isFirstChecked()); then
             *      if (checkUnknown(pendingTx, pendingTxList); then
             *          txService.changeTxStatus(pendingTx.txid, TxStatus.SENT_UNKNOWN)
             *      fi
             * fi
             *  아니면 pending
             */
            /**
             * 1. 첫 검사때 없으면 UNKNOWN
             * 2. 첫 검사때 있으면 PENDING
             *
             * 이후로 PENDING 이었다가 없어졌다
             * A)
             * -> receipt검사해서 있으면 SUCCESS or FAIL
             * -> receipt검사해서 없으면 LOST
             * LOST도 A) 반복
             */
        }
    }

    private boolean checkCanceled(SentTx sentTx) throws ExecutionException, InterruptedException {
        BigInteger confirmedNonce = web3jUtil.getNonce(sentTx.getFrom());
        if (sentTx.getNonce().compareTo(confirmedNonce) <= 0) {
            return true;
        }
        return false;
    }

    // pending if not unknown
    private boolean checkUnknown(PendingTx pendingTx, List<String> pendingList) throws Exception {
        for(String pendingTxId : pendingList) {
            if (pendingTxId.equals(pendingTx.getTxId())) {
                return false;
            }
        }
        return true;
    }
}
