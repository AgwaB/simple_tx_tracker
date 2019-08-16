package com.simple.tracker.app.value;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EthForm {
    String fromPrivKey;
    BigInteger nonce;
    String to;
    String gasPrice;
    String gasLimit;
    long value;
}
