package com.simple.tracker.workspace;

import com.simple.tracker.app.UnconfirmedTx;
import com.simple.tracker.app.parity.ParityService;
import com.simple.tracker.app.service.TxService;
import com.simple.tracker.app.util.Web3jUtil;
import com.simple.tracker.app.value.TxLog;
import com.simple.tracker.app.value.TxStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;

@Component
public class UnconfirmedTxScheduler {
    private static final int BREAK_TIME = 15 * 1000;

    private BlockingQueue<UnconfirmedTx> unconfirmedTxQueue;
    private ParityService parityService;
    private TxService txService;

    public UnconfirmedTxScheduler(
            @Qualifier("unconfirmedTxQueue") BlockingQueue<UnconfirmedTx> unconfirmedTxQueue,
            ParityService parityService,
            TxService txService
    ) {
        this.unconfirmedTxQueue = unconfirmedTxQueue;
        this.parityService = parityService;
        this.txService = txService;
    }

    @Scheduled(fixedDelay = BREAK_TIME)
    public void run() {
        Set<String> pendingTxsHash = parityService.getPendingTransactionsHash();
        for (UnconfirmedTx unconfirmedTx : unconfirmedTxQueue) {
            try {
                if(txService.checkIsConfirmed(unconfirmedTx)) {
                    txService.changeTxStatus(
                            unconfirmedTx,
                            txService.getConfirmedStatus(unconfirmedTx.getTxId())
                    );
                    unconfirmedTxQueue.remove(unconfirmedTx);
                    continue;
                }

                if(txService.checkCanceled(unconfirmedTx)) {
                    txService.changeTxStatus(unconfirmedTx, TxStatus.SENT_CANCEL);
                    unconfirmedTxQueue.remove(unconfirmedTx);
                    continue;
                }

                if(txService.checkIsUnknown(unconfirmedTx, pendingTxsHash)) {
                    txService.changeTxStatus(unconfirmedTx, TxStatus.SENT_UNKNOWN);
                    continue;
                }

                if(txService.checkIsLost(unconfirmedTx, pendingTxsHash)) {
                    txService.changeTxStatus(unconfirmedTx, TxStatus.LOST);
                    continue;
                }

                txService.changeTxStatus(unconfirmedTx, TxStatus.PENDING);
                unconfirmedTx.increadCheckedCnt();

                /**
                 * check confirmed
                 * check unknown
                 * check lost(it is not first checking and no tx in pending list)
                 * check pending(it is in pending list)
                 */
            } catch (ExecutionException e) {
                // get nonce
                TxLog.error(e.getMessage());
            } catch (InterruptedException e) {
                // get nonce
                TxLog.error(e.getMessage());
            } catch (Exception e) {
                // send receipt request
                TxLog.error(e.getMessage());
            }
        }
    }

//    @Scheduled(fixedDelay = PENDING_BREAK_TIME)
    public void runPendingSchedule() {
        /**
         * pendingTxList = getPendingTxList;
         *
          */
//        for (PendingTx pendingTx : pendingTxQueue) {
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
//        }
    }
}
