package com.simple.tracker.contract;

import java.math.BigInteger;

public class ContractGasProvider implements org.web3j.tx.gas.ContractGasProvider {
    private final BigInteger gasPrice;
    private final BigInteger gasLimit;

    public ContractGasProvider(BigInteger gasPrice, BigInteger gasLimit) {
        this.gasPrice = gasPrice;
        this.gasLimit = gasLimit;
    }
    @Override
    public BigInteger getGasPrice(String contractFunc) {
        return this.gasPrice;
    }

    @Override
    public BigInteger getGasPrice() {
        return this.gasPrice;
    }

    @Override
    public BigInteger getGasLimit(String contractFunc) {
        return this.gasLimit;
    }

    @Override
    public BigInteger getGasLimit() {
        return this.gasLimit;
    }
}
