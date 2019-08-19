package com.simple.tracker.app.parity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class ParityRpcResponse {
    private Object result;
    private String jsonrpc;
    private int id;
}
