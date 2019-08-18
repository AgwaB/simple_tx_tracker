package com.simple.tracker.app.value;

public enum TxStatus {
    SENT_CANCEL, // send but canceled because same nonce (same as )
    SENT_UNKNOWN, // send and not SENT_CANCEL and don't exist in txpool (maybe network failure or can be seen in txpool a lot later )
    PENDING, // pending transaction in txpool
    SUCCESS, // success to send
    FAIL, // (the transaction which included in block) when there is not enough gas when run the contract or an exception occurs during execution
    LOST // tx entered txpool and exited with memory problem
}
