package com.simple.tracker.app;

import com.simple.tracker.app.service.TxContractService;
import com.simple.tracker.app.value.ContractFrom;
import org.web3j.crypto.Credentials;

public class ContractRequest implements TxRequest {
    private TxContractService txContractService;
    private ContractFrom contractFrom;
    private Credentials from;

    public ContractRequest(
            TxContractService txContractService,
            ContractFrom contractFrom,
            Credentials from
    ) {
        this.txContractService = txContractService;
        this.contractFrom = contractFrom;
        this.from = from;
    }


    @Override
    public void execute() throws Exception {
        txContractService.sendContract(
                from,
                contractFrom.getContractBinary(),
                contractFrom.getContractAddress(),
                contractFrom.getGasPrice(),
                contractFrom.getGasLimit()
        );
    }
}
