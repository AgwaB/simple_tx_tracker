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
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UnconfirmedTx {
    private String txId;
    private String from;
    private BigInteger nonce;
    private boolean isFirstChecked = true;

    public UnconfirmedTx(String txId, String from, BigInteger nonce) {
        this.txId = txId;
        this.from = from;
        this.nonce = nonce;
    }

    public boolean isFirstChecked() {
        return isFirstChecked;
    }
}
