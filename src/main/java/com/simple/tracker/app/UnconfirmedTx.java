package com.simple.tracker.app;

import com.simple.tracker.app.service.TxService;
import com.simple.tracker.app.value.TxStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import java.math.BigInteger;

@Getter
public class UnconfirmedTx {
    private String txId;
    private boolean isContract;
    private TxStatus txStatus;
    private BigInteger blockNumber;
    private BigInteger nonce;
    private String from;
    private String to;
    private BigInteger value;
    private String data;
    private int checkedCnt = 0;

    public UnconfirmedTx(String txId, String from, BigInteger nonce, TxStatus txStatus) {
        this.txId = txId;
        this.from = from;
        this.nonce = nonce;
        this.txStatus = txStatus;
    }

    public boolean isFirstChecked() {
        if (checkedCnt == 0) {
            return true;
        }
        return false;
    }

    public void increadCheckedCnt() {
        this.checkedCnt++;
    }

    public void changeTxStatus(TxStatus txStatus) {
        this.txStatus = txStatus;
    }
}
