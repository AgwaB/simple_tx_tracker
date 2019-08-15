package com.simple.tracker.app.value;

import java.math.BigInteger;

public class Web3jDefaultValue {
    public static final BigInteger ACCOUNT_UNLOCK_DURATION = BigInteger.valueOf(30);
    public static final int DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH = 1;
    public static final int POLLING_FREQUENCY = 1 * 1000;
    public static final BigInteger GAS_PRICE = BigInteger.valueOf(22_000_000_000L);
    public static final BigInteger GAS_LIMIT = BigInteger.valueOf(4_300_000);
    public static final BigInteger CONTRACT_VALUE = BigInteger.valueOf(0);
}
