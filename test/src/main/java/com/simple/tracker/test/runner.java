package com.simple.tracker.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;

import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

@Component
public class runner implements ApplicationRunner {
    private final String baseUrl = "http://localhost:8080";
    private final int count = 10;
    @Autowired
    private Web3j web3j;
    @Autowired
    private RestTemplate restTemplate;

    EthForm withoutNonce = new EthForm(
            "90D78383274C425BB7F23346410E537C834F9B3569F65ABA19C3787E8EB1875B",
            new BigInteger("-1"),
            "0x33ffe564A61d48408b5b8Db0c112e7Cc79d023a5",
            "20000000000",
            "21000",
            1
    );

    EthForm withNonce = new EthForm(
            "90D78383274C425BB7F23346410E537C834F9B3569F65ABA19C3787E8EB1875B",
            null,
            "0x33ffe564A61d48408b5b8Db0c112e7Cc79d023a5",
            "20000000000",
            "21000",
            1
    );

    ContractFrom contractFrom = new ContractFrom(
            "90D78383274C425BB7F23346410E537C834F9B3569F65ABA19C3787E8EB1875B",
            "0x6080604052348015600f57600080fd5b50606a8061001e6000396000f3fe6080604052348015600f57600080fd5b506004361060285760003560e01c806326121ff014602d575b600080fd5b60336035565b005b6000603c57fe5b56fea165627a7a72305820b3d7024a101666dfed41c753fbd73880ccc6a765a0a2957b9260841c5144f9450029",
            "0x0DaD767707C9243CE84bdc6A17f6cA5F84D1ff4E",
            "20000000000",
            "40000"
    );

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Credentials credentials = Credentials.create("90D78383274C425BB7F23346410E537C834F9B3569F65ABA19C3787E8EB1875B");

        BigInteger nonce = getNonce(credentials.getAddress());
        // [ETH] SENT_CANCEL(nonce 수동 설정)
        // expect : count / 2 만큼 CANCEL 발생
        for (int i = 0 ; i < count / 2 ; i++) {
            withNonce.setValue(50);
            withNonce.setTo("0x33ffe564A61d48408b5b8Db0c112e7Cc79d023a5");
            withNonce.setNonce(nonce);
            sendEth(withNonce);

            Thread.sleep(5000);

            withNonce.setValue(100);
            // to me
            withNonce.setTo("0x453011A4F948b22762290C1F4de3e2210C311c06");
            withNonce.setGasLimit("40000");
            withNonce.setGasPrice("40000000000");
            sendEth(withNonce);
            nonce = nonce.add(BigInteger.ONE);
            Thread.sleep(5000);
        }

        // [ETH] SUCCESS(nonce 자동 설정)
        for (int i = 0 ; i < count ; i++) {
            sendEth(withoutNonce);
        }

        Thread.sleep(20 * 1000);

        // [ETH] SENT_UNKNOWN(nonce 수동 설정, invalid 한 tx)
        for (int i = 0 ; i < count ; i++) {
            withNonce.setNonce(BigInteger.valueOf(99999 + i));
            sendEth(withNonce);
        }

        Thread.sleep(20 * 1000);

        // [CONTRACT] FAIL(fail을 유발하는 contract)
        for (int i = 0 ; i < count ; i++) {
            sendContract(contractFrom);
        }
    }

    private void sendEth(EthForm ethForm) {
        restTemplate.postForObject(baseUrl + "/tx/eth", ethForm, EthForm.class);
    }

    private void sendContract(ContractFrom contractFrom) {
        restTemplate.postForObject(baseUrl + "/tx/contract", contractFrom, ContractFrom.class);
    }

    private BigInteger getNonce(String address) throws ExecutionException, InterruptedException {
        EthGetTransactionCount ethGetTransactionCount =
                web3j.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST)
                        .sendAsync()
                        .get();

        return ethGetTransactionCount.getTransactionCount();
    }
}
