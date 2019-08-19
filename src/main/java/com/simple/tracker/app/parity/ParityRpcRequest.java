package com.simple.tracker.app.parity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Getter
public class ParityRpcRequest {
    private final String jsonrpc;
    private final int id;
    private final String method;
    private final List<Object> params;

    @JsonCreator
    public ParityRpcRequest(@JsonProperty String method, @JsonProperty List<Object> params) {
        this.jsonrpc = "2.0";
        this.id = 1;
        this.method = method;
        this.params = params;
    }

    @JsonCreator
    public ParityRpcRequest(@JsonProperty String method) {
        this.jsonrpc = "2.0";
        this.id = 1;
        this.method = method;
        this.params = Collections.emptyList();
    }
}
