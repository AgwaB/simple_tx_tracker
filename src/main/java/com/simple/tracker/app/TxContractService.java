package com.simple.tracker.app;

import com.simple.tracker.app.util.Web3jUtil;
import com.simple.tracker.app.value.TxLog;
import com.simple.tracker.app.value.TxStatus;
import com.simple.tracker.config.InitConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Optional;

@Service
public class TxContractService {
    Logger logger = LoggerFactory.getLogger("txLogger");

    private static final BigInteger GAS_PRICE = BigInteger.valueOf(22_000_000_000L);
    private static final BigInteger GAS_LIMIT = BigInteger.valueOf(500000);
    private static final BigInteger CONTRACT_VALUE = BigInteger.valueOf(0);

    @Autowired
    private Admin web3j;
    @Autowired
    private Web3jUtil web3jUtil;
    @Autowired
    private InitConfig initConfig;

    public void sendContract(Credentials from, String contractAddress) throws Exception {
        BigInteger nonce = web3jUtil.getNonce(from.getAddress());
        // call our getter
        Function fFunction = createfFunction();
//        String responseValue = callSmartContractFunction(from, fFunction, contractAddress);
//        System.out.println(responseValue);
        String encodedFunction = FunctionEncoder.encode(fFunction);

        Transaction transaction = Transaction.createFunctionCallTransaction(
                from.getAddress(), nonce, GAS_PRICE, GAS_LIMIT, contractAddress, CONTRACT_VALUE, encodedFunction);
        org.web3j.protocol.core.methods.response.EthSendTransaction transactionResponse =
                web3j.ethSendTransaction(transaction).sendAsync().get();

        String transactionHash = transactionResponse.getTransactionHash();
        TxLog.info("TX", "hash", transactionHash, "status", TxStatus.PENDING.toString());

        TransactionReceipt transactionReceipt = web3jUtil.waitForTransactionReceipt(transactionHash);
        TxLog.info("TX", "hash", transactionHash, "status", TxStatus.SUCCESS.toString());
    }

    private String callSmartContractFunction(Credentials from, Function function, String contractAddress)
            throws Exception {

        String encodedFunction = FunctionEncoder.encode(function);

        org.web3j.protocol.core.methods.response.EthCall response =
                web3j.ethCall(
                        Transaction.createEthCallTransaction(
                                from.getAddress(), contractAddress, encodedFunction),
                        DefaultBlockParameterName.LATEST)
                        .sendAsync()
                        .get();

        return response.getValue();
    }

    private Function createfFunction() {
        return new Function(
                "f",
                Collections.emptyList(),
                Collections.singletonList(new TypeReference<Utf8String>() {}));
    }
}
