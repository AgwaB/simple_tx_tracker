package com.simple.tracker.Web3jProvider;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.tx.ManagedTransaction;
import org.web3j.tx.TransactionManager;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

public class NonceTransfer extends ManagedTransaction {
    public NonceTransfer(Web3j web3j, TransactionManager transactionManager) {
        super(web3j, transactionManager);
    }

    public RemoteCall<TransactionReceipt> sendFunds(
            String toAddress,
            BigDecimal value,
            Convert.Unit unit,
            BigInteger gasPrice,
            BigInteger gasLimit) {
        return new RemoteCall<>(() -> send(toAddress, value, unit, gasPrice, gasLimit));
    }

    private TransactionReceipt send(
            String toAddress,
            BigDecimal value,
            Convert.Unit unit,
            BigInteger gasPrice,
            BigInteger gasLimit)
            throws IOException, InterruptedException, TransactionException {

        BigDecimal weiValue = Convert.toWei(value, unit);
        if (!Numeric.isIntegerValue(weiValue)) {
            throw new UnsupportedOperationException(
                    "Non decimal Wei value provided: "
                            + value
                            + " "
                            + unit.toString()
                            + " = "
                            + weiValue
                            + " Wei");
        }

        String resolvedAddress = ensResolver.resolve(toAddress);
        return send(resolvedAddress, "", weiValue.toBigIntegerExact(), gasPrice, gasLimit);
    }
}
