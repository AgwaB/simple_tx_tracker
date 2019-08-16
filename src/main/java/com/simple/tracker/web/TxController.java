package com.simple.tracker.web;

import com.simple.tracker.app.*;
import com.simple.tracker.app.retrofit.RequestUtil;
import com.simple.tracker.app.retrofit.TxService;
import com.simple.tracker.app.retrofit.value.Response;
import com.simple.tracker.app.service.CredentialsService;
import com.simple.tracker.app.service.TxContractService;
import com.simple.tracker.app.service.TxEthService;
import com.simple.tracker.app.value.ContractFrom;
import com.simple.tracker.app.value.EthForm;
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
    private BlockingQueue<TxRequest> txQueue;
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
//        Credentials credentials = credentialsService.getCredentialsByPrivKey(contractFrom.getFromPrivKey());
//        try {
//            txContractService.sendContract(
//                    credentials,
//                    contractFrom.getContractBinary(),
//                    contractFrom.getContractAddress(),
//                    contractFrom.getGasPrice(),
//                    contractFrom.getGasLimit()
//            );
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
