package com.simple.tracker.app;

import com.simple.tracker.app.service.TxEthService;
import com.simple.tracker.app.value.EthForm;
import org.web3j.crypto.Credentials;

public class EthRequest implements TxRequest {
    private TxEthService txEthService;
    private EthForm ethForm;
    private Credentials from;

    public EthRequest(
            TxEthService txEthService,
            EthForm ethForm,
            Credentials from
    ) {
        this.txEthService = txEthService;
        this.ethForm = ethForm;
        this.from = from;
    }

    @Override
    public void execute() throws Exception {
        // negative nonce means not send with nonce.
        if (ethForm.getNonce().intValue() < 0) {
            txEthService.sendTxWithoutNonce(
                    from,
                    ethForm
            );
        } else {
            txEthService.sendTxWithNonce(
                    from,
                    ethForm
            );
        }
    }
}
