package com.simple.tracker.web;

import com.simple.tracker.app.CredentialsService;
import com.simple.tracker.app.TransactionService;
import com.simple.tracker.app.retrofit.RequestUtil;
import com.simple.tracker.app.retrofit.TxService;
import com.simple.tracker.app.retrofit.value.Response;
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

    @PostMapping()
    public void send(@RequestBody TxForm txForm) {
        txQueue.add(txForm);
    }
}
