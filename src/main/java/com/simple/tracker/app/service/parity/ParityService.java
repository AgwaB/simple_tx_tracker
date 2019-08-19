package com.simple.tracker.app.service.parity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simple.tracker.app.value.PendingTx;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class ParityService {
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final String parityBaseUrl;
    private final HttpHeaders parityHttpHeader;

    public ParityService(RestTemplate restTemplate,
                         @Qualifier("parityBaseUrl") String parityBaseUrl,
                         @Qualifier("parityHttpHeaders") HttpHeaders parityHttpHeader) {
        this.restTemplate = restTemplate;
        this.parityBaseUrl = parityBaseUrl;
        this.parityHttpHeader = parityHttpHeader;
        this.objectMapper = new ObjectMapper();
    }

    public HashSet<PendingTx> getPendingTransactions() {
        ParityRpcRequest request = new ParityRpcRequest("parity_pendingTransactions", Arrays.asList());
        ParityRpcResponse response = getResponse(request);
        List<LinkedHashMap> txMaps = (List<LinkedHashMap>) response.getResult();

        HashSet<PendingTx> pendingTxs = new HashSet<>();

        for (LinkedHashMap tx : txMaps) {
            PendingTx pendingTx = objectMapper.convertValue(tx, PendingTx.class);
            pendingTxs.add(pendingTx);
        }

        return pendingTxs;
    }

    public HashSet<String> getPendingTransactionsHash() {
        ParityRpcRequest request = new ParityRpcRequest("parity_pendingTransactions", Arrays.asList());
        ParityRpcResponse response = getResponse(request);
        List<LinkedHashMap> txMaps = (List<LinkedHashMap>) response.getResult();

        HashSet<String> pendingTxsHash = new HashSet<>();

        for (LinkedHashMap tx : txMaps) {
            PendingTx pendingTx = objectMapper.convertValue(tx, PendingTx.class);
            pendingTxsHash.add(pendingTx.getTxId());
        }

        return pendingTxsHash;
    }

    private ParityRpcResponse getResponse(ParityRpcRequest request) {
        HttpEntity<ParityRpcRequest> requestHttpEntity = new HttpEntity<>(request, parityHttpHeader);
        try {
            ParityRpcResponse response = restTemplate.postForObject(parityBaseUrl, requestHttpEntity, ParityRpcResponse.class);
            return response;
        } catch (RestClientResponseException ex) {
            throw new RuntimeException(ex.getResponseBodyAsString());
        }
    }
}
