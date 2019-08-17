package com.simple.tracker.app.service;

import com.simple.tracker.app.util.Web3jUtil;
import com.simple.tracker.app.value.TxLog;
import com.simple.tracker.app.value.TxStatus;
import com.simple.tracker.config.InitConfig;
import com.simple.tracker.Web3jProvider.Contract;
import com.simple.tracker.Web3jProvider.ContractGasProvider;
import com.simple.tracker.domain.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.FastRawTransactionManager;
import org.web3j.tx.response.QueuingTransactionReceiptProcessor;

import java.math.BigInteger;

@Service
public class TxContractService {
    @Autowired
    private Admin web3j;
    @Autowired
    private TxService txService;
    @Autowired
    @Qualifier("queuingTxReceipt")
    private QueuingTransactionReceiptProcessor queuingTransactionReceiptProcessor;

    @Async("txAsyncExecutor")
    public synchronized void sendContract(Credentials from, String contractBinary, String contractAddress, String gasPrice, String gasLimit) throws Exception {
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

        txService.processTx(
                Transaction.builder()
                        .txId(transactionReceipt.getTransactionHash())
                        .isContract(true)
                        .txStatus(TxStatus.PENDING)
                        .from(from.getAddress())
                        .to(contractAddress)
                        .build()
        );
    }
}
