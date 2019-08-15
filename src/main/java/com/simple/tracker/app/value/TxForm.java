package com.simple.tracker.app.value;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TxForm {
    String fromPrivKey;
    BigInteger nonce;
    String to;
    long value;
}
