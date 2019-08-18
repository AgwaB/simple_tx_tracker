package com.simple.tracker.app.tx;

import com.simple.tracker.app.service.TxService;
import com.simple.tracker.app.value.TxStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import java.math.BigInteger;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class UnconfirmedTx {
    private String txId;
}
