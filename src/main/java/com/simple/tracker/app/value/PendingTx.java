package com.simple.tracker.app.value;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import javax.persistence.Id;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PendingTx {
    @Id
    @JsonProperty("hash")
    private String txId;
}
