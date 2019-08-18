package com.simple.tracker.app.tx;

import com.simple.tracker.app.util.Web3jUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PendingTx extends UnconfirmedTx {
    private boolean isFirstChecked = true;

    public PendingTx(String txId) {
        super(txId);
    }

    public boolean isFirstChecked() {
        return isFirstChecked;
    }
}
