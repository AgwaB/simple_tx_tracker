package com.simple.tracker.app;

import com.simple.tracker.app.util.Web3jUtil;
import com.simple.tracker.app.value.TxLog;
import com.simple.tracker.app.value.TxStatus;
import com.simple.tracker.config.InitConfig;
import com.simple.tracker.contract.Contract;
import com.simple.tracker.contract.ContractGasProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.FastRawTransactionManager;
import org.web3j.tx.response.Callback;
import org.web3j.tx.response.QueuingTransactionReceiptProcessor;

import java.math.BigInteger;
import java.util.Collections;

import static com.simple.tracker.app.value.Web3jDefaultValue.*;

@Service
public class TxContractService {
    Logger logger = LoggerFactory.getLogger("txLogger");

    @Autowired
    private Admin web3j;
    @Autowired
    private Web3jUtil web3jUtil;
    @Autowired
    private InitConfig initConfig;
    @Autowired
    @Qualifier("QueuingTxReceipt")
    private QueuingTransactionReceiptProcessor queuingTransactionReceiptProcessor;

    @Async("txAsyncExecutor")
    public void sendContract(Credentials from, String contractAddress, String gasPrice, String gasLimit) throws Exception {
        BigInteger nonce = web3jUtil.getNonce(from.getAddress());
        // call our getter
        Function fFunction = createfFunction();
//        String responseValue = callSmartContractFunction(from, fFunction, contractAddress);
//        System.out.println(responseValue);
        String encodedFunction = FunctionEncoder.encode(fFunction);

        Transaction transaction = Transaction.createFunctionCallTransaction(
                from.getAddress(), nonce, new BigInteger(gasPrice), new BigInteger(gasLimit), contractAddress, CONTRACT_VALUE, encodedFunction);
        org.web3j.protocol.core.methods.response.EthSendTransaction transactionResponse =
                web3j.ethSendTransaction(transaction).sendAsync().get();

        String transactionHash = transactionResponse.getTransactionHash();
        TxLog.info("TX", "hash", transactionHash, "status", TxStatus.PENDING.toString());

        TransactionReceipt transactionReceipt = web3jUtil.waitForTransactionReceipt(transactionHash);
        if (transactionReceipt.getStatus().equals("0x1")) {
            TxLog.info("TX", "hash", transactionHash, "status", TxStatus.SUCCESS.toString());
        }

        if (transactionReceipt.getStatus().equals("0x0")) {
            TxLog.info("TX", "hash", transactionHash, "status", TxStatus.FAIL.toString());
        }
    }

    @Async("txAsyncExecutor")
    public void sendContract2(Credentials from, String contractBinary, String contractAddress, String gasPrice, String gasLimit) throws Exception {
        FastRawTransactionManager transactionManager = new FastRawTransactionManager(
                web3j,
                from,
                queuingTransactionReceiptProcessor
        );
        ContractGasProvider contractGasProvider
                = new ContractGasProvider(new BigInteger(gasPrice), new BigInteger(gasLimit));
        Contract contract = new Contract(
                contractBinary,
                contractAddress,
                web3j,
                transactionManager,
                contractGasProvider
        );

        TransactionReceipt transactionReceipt
                = contract.performTransaction(new Address(contractAddress), "f", Uint256.DEFAULT).send();
        TxLog.info("TX", "hash", transactionReceipt.getTransactionHash(), "status", TxStatus.PENDING.toString());
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
