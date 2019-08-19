package com.simple.tracker.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class InitConfig {
    @Value("${node.mode}")
    private String mode;
    @Value("${node.ganache.url}")
    private String ganacheUrl;
    @Value("${node.ganache.port}")
    private String ganachePort;
    @Value("${node.infura.url}")
    private String infuraUrl;
    @Value("${node.infura.key}")
    private String infuraKey;
    @Value("${node.parity.url}")
    private String parityUrl;
    @Value("${node.parity.port}")
    private String parityPort;
    @Value("${etherscan.api.key}")
    private String etherscanKey;
    @Value("${tracker.address}")
    private String address;
    @Value("${tx.thread-pool}")
    private int txThreadPoolSize;
    @Value("${tx.queue-size}")
    private int txQueueSize;

    public String getAddress() {
        return "0x" + this.address.toLowerCase();
    }
}
