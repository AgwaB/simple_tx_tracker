package com.simple.tracker.web;

import com.simple.tracker.app.CredentialsService;
import com.simple.tracker.app.TxContractService;
import com.simple.tracker.app.TxEthService;
import com.simple.tracker.app.retrofit.RequestUtil;
import com.simple.tracker.app.retrofit.TxService;
import com.simple.tracker.app.retrofit.value.Response;
import com.simple.tracker.app.value.ContractFrom;
import com.simple.tracker.app.value.TxForm;
import com.simple.tracker.config.InitConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import org.web3j.crypto.Credentials;
import retrofit2.Call;

import java.util.concurrent.BlockingQueue;

@RestController
@RequestMapping("/tx")
public class TxController {
    @Autowired
    private InitConfig initConfig;
    @Autowired
    @Qualifier("linkedBlockingQueue")
    private BlockingQueue txQueue;
    @Autowired
    private TxEthService txEthService;
    @Autowired
    private TxContractService txContractService;
    @Autowired
    private CredentialsService credentialsService;

    @GetMapping()
    public Response test() {
        TxService txService = RequestUtil.createService(TxService.class);
        Call<Response> txList = txService.getTransactions(
                "account",
                "txlist",
                "0x453011a4f948b22762290c1f4de3e2210c311c06",
                0,
                99999999,
                "asc",
                initConfig.getEtherscanKey()
        );
        Response response = RequestUtil.requestSync(txList).get();
        return response;
    }

    // TODO
    @PostMapping()
    public void sendWithoutNonce(@RequestBody TxForm txForm) {
        txQueue.add(txForm);
    }

    @PostMapping("/nonce")
    public void send(@RequestBody TxForm txForm) {
        Credentials credentials = credentialsService.getCredentialsByPrivKey(txForm.getFromPrivKey());
        try {
            txEthService.sendTxWithoutNonce(
                    credentials,
                    txForm.getTo(),
                    txForm.getValue()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/contract")
    public void callContract(@RequestBody ContractFrom contractFrom) {
        Credentials credentials = credentialsService.getCredentialsByPrivKey(contractFrom.getFromPrivKey());
        try {
            txContractService.sendContract(
                    credentials,
                    contractFrom.getContractAddress(),
                    contractFrom.getGasPrice(),
                    contractFrom.getGasLimit()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/contract2")
    public void callContract2(@RequestBody ContractFrom contractFrom) {
        Credentials credentials = credentialsService.getCredentialsByPrivKey(contractFrom.getFromPrivKey());
        try {
            txContractService.sendContract2(
                    credentials,
                    contractFrom.getContractBinary(),
                    contractFrom.getContractAddress(),
                    contractFrom.getGasPrice(),
                    contractFrom.getGasLimit()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
