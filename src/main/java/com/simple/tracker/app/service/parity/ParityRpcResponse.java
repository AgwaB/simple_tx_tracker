package com.simple.tracker.app.service.parity;

import lombok.Getter;

@Getter
public class ParityRpcResponse {
    private Object result;
    private String jsonrpc;
    private int id;
}
