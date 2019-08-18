package com.simple.tracker.app.tx;

import com.simple.tracker.app.util.Web3jUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SentTx extends UnconfirmedTx {
    private String from;
    private BigInteger nonce;

    public SentTx(String txId, String from, BigInteger nonce) {
        super(txId);
        this.from = from;
        this.nonce = nonce;
    }
}
