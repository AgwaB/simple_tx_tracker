package com.simple.tracker.app;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.simple.tracker.app.value.TxStatus;
import lombok.Getter;

import javax.persistence.Id;
import java.math.BigInteger;
import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PendingTx {
    @Id
    @JsonProperty("hash")
    private String txId;
}
