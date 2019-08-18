package com.simple.tracker.domain;

import com.simple.tracker.app.value.TxStatus;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigInteger;

@Entity
@Table(name = "transactions")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Transaction {
    @Id
    @Column(name = "txId")
    private String txId;
    @Column(name = "isContract")
    private boolean isContract;
    @Column(name = "txStatus")
    private TxStatus txStatus;
    @Column(name = "blockNumber")
    private BigInteger blockNumber;
    @Column(name = "nonce")
    private BigInteger nonce;
    @Column(name = "fromAddress")
    private String from;
    @Column(name = "toAddress")
    private String to;
    @Column(name = "value")
    private BigInteger value;
    @Column(name = "data")
    private String data;

    public void changeStatus(TxStatus txStatus) {
        this.txStatus = txStatus;
    }
}
