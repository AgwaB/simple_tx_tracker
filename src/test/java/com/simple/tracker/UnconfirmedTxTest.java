package com.simple.tracker;

import com.simple.tracker.app.UnconfirmedTx;
import com.simple.tracker.app.parity.ParityService;
import com.simple.tracker.app.service.TxService;
import com.simple.tracker.app.util.Web3jUtil;
import com.simple.tracker.app.value.TxStatus;
import com.simple.tracker.workspace.UnconfirmedTxScheduler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.BlockingQueue;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UnconfirmedTxTest {
    @Autowired
    @Qualifier("unconfirmedTxQueue")
    private BlockingQueue<UnconfirmedTx> unconfirmedTxQueue;
    private TxService txService;
    private ParityService parityService;
    private UnconfirmedTxScheduler unconfirmedTxScheduler;

    @Before
    public void setup() {
        parityService = mock(ParityService.class);
        txService = mock(TxService.class);
    }

    @Test
    public void confirmedTest() throws Exception {
        // given
        UnconfirmedTx unconfirmedTx1 = new UnconfirmedTx("pending", null, null, TxStatus.PENDING);
        UnconfirmedTx unconfirmedTx2 = new UnconfirmedTx("success", null, null, TxStatus.PENDING);
        UnconfirmedTx unconfirmedTx3 = new UnconfirmedTx("fail", null, null, TxStatus.PENDING);
        unconfirmedTxQueue.addAll(Arrays.asList(
                unconfirmedTx1,
                unconfirmedTx2,
                unconfirmedTx3
        ));

        // when
        when(parityService.getPendingTransactionsHash()).thenReturn(new HashSet<>());

        when(txService.checkIsConfirmed(unconfirmedTx1)).thenReturn(false);
        when(txService.checkIsConfirmed(unconfirmedTx2)).thenReturn(true);
        when(txService.checkIsConfirmed(unconfirmedTx3)).thenReturn(true);

        when(txService.getConfirmedStatus(unconfirmedTx2.getTxId())).thenReturn(TxStatus.SUCCESS);
        when(txService.getConfirmedStatus(unconfirmedTx3.getTxId())).thenReturn(TxStatus.FAIL);

        doAnswer((Answer) invocation -> {
            unconfirmedTx2.changeTxStatus(invocation.getArgument(1));
            return null;
                }).when(txService).changeTxStatus(unconfirmedTx2, TxStatus.SUCCESS);

        doAnswer((Answer) invocation -> {
            unconfirmedTx3.changeTxStatus(invocation.getArgument(1));
            return null;
        }).when(txService).changeTxStatus(unconfirmedTx3, TxStatus.FAIL);

        unconfirmedTxScheduler = new UnconfirmedTxScheduler(
                unconfirmedTxQueue,
                parityService,
                txService
        );

        unconfirmedTxScheduler.run();

        // then
        Assert.assertEquals(1, unconfirmedTxQueue.size());
        verify(txService).changeTxStatus(unconfirmedTx2, TxStatus.SUCCESS);
        verify(txService).changeTxStatus(unconfirmedTx3, TxStatus.FAIL);
        Assert.assertEquals(TxStatus.SUCCESS, unconfirmedTx2.getTxStatus());
        Assert.assertEquals(TxStatus.FAIL, unconfirmedTx3.getTxStatus());
    }

    @Test
    public void lostToPendingTest() {
    }

    @Test
    public void integrationTest() {
    }
}
