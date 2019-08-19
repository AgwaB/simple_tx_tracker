package com.simple.tracker.test;

import java.math.BigInteger;

public class EthForm {
    String fromPrivKey;
    BigInteger nonce;
    String to;
    String gasPrice;
    String gasLimit;
    long value;

    public EthForm(String fromPrivKey, BigInteger nonce, String to, String gasPrice, String gasLimit, long value) {
        this.fromPrivKey = fromPrivKey;
        this.nonce = nonce;
        this.to = to;
        this.gasPrice = gasPrice;
        this.gasLimit = gasLimit;
        this.value = value;
    }

    public String getFromPrivKey() {
        return fromPrivKey;
    }

    public void setFromPrivKey(String fromPrivKey) {
        this.fromPrivKey = fromPrivKey;
    }

    public BigInteger getNonce() {
        return nonce;
    }

    public void setNonce(BigInteger nonce) {
        this.nonce = nonce;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(String gasPrice) {
        this.gasPrice = gasPrice;
    }

    public String getGasLimit() {
        return gasLimit;
    }

    public void setGasLimit(String gasLimit) {
        this.gasLimit = gasLimit;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
}
