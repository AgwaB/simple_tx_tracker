package com.simple.tracker.test;

public class ContractFrom {
    String fromPrivKey;
    String contractBinary;
    String contractAddress;
    String gasPrice;
    String gasLimit;

    public ContractFrom(String fromPrivKey, String contractBinary, String contractAddress, String gasPrice, String gasLimit) {
        this.fromPrivKey = fromPrivKey;
        this.contractBinary = contractBinary;
        this.contractAddress = contractAddress;
        this.gasPrice = gasPrice;
        this.gasLimit = gasLimit;
    }

    public String getFromPrivKey() {
        return fromPrivKey;
    }

    public void setFromPrivKey(String fromPrivKey) {
        this.fromPrivKey = fromPrivKey;
    }

    public String getContractBinary() {
        return contractBinary;
    }

    public void setContractBinary(String contractBinary) {
        this.contractBinary = contractBinary;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
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
}
