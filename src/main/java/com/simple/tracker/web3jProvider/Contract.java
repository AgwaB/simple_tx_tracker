package com.simple.tracker.web3jProvider;

import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

import java.util.Arrays;
import java.util.Collections;

public class Contract extends org.web3j.tx.Contract {
    public Contract(
            String contractBinary,
            String contractAddress,
            Web3j web3j,
            TransactionManager transactionManager,
            ContractGasProvider gasProvider
    ) {
        super(
                contractBinary,
                contractAddress,
                web3j,
                transactionManager,
                gasProvider
        );
    }

    public Contract(
            String contractBinary,
            String contractAddress,
            Web3j web3j,
            Credentials credentials,
            ContractGasProvider gasProvider
    ) {
        super(
                contractBinary,
                contractAddress,
                web3j,
                credentials,
                gasProvider
        );
    }

    public RemoteCall<TransactionReceipt> performTransaction(Address address, String functionName, Uint256 amount) {
        Function function =
                new Function(
                        functionName,
                        Arrays.<Type>asList(address, amount),
                        Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }
}
