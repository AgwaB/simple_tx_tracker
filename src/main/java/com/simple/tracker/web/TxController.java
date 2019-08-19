package com.simple.tracker.web;

import com.simple.tracker.app.ContractRequest;
import com.simple.tracker.app.EthRequest;
import com.simple.tracker.app.TxRequest;
import com.simple.tracker.app.service.CredentialsService;
import com.simple.tracker.app.service.TxContractService;
import com.simple.tracker.app.service.TxEthService;
import com.simple.tracker.app.value.ContractFrom;
import com.simple.tracker.app.value.EthForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.crypto.Credentials;

import java.util.concurrent.BlockingQueue;

@RestController
@RequestMapping("/tx")
public class TxController {
    @Autowired
    @Qualifier("requestTxQueue")
    private BlockingQueue<TxRequest> txQueue;
    @Autowired
    private TxEthService txEthService;
    @Autowired
    private TxContractService txContractService;
    @Autowired
    private CredentialsService credentialsService;

    @PostMapping("/eth")
    public void sendEther(@RequestBody EthForm ethForm) {
        Credentials credentials = credentialsService.getCredentialsByPrivKey(ethForm.getFromPrivKey());
        EthRequest ethRequest = new EthRequest(txEthService, ethForm, credentials);
        txQueue.add(ethRequest);
    }

    @PostMapping("/contract")
    public void callContract(@RequestBody ContractFrom contractFrom) {
        Credentials credentials = credentialsService.getCredentialsByPrivKey(contractFrom.getFromPrivKey());
        ContractRequest contractRequest = new ContractRequest(txContractService, contractFrom, credentials);
        txQueue.add(contractRequest);
    }
}
