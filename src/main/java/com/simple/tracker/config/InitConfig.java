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
    @Value("${node.ropsten.url}")
    private String ropstenUrl;
    @Value("${node.ropsten.key}")
    private String ropstenKey;
    @Value("${etherscan.api.key}")
    private String etherscanKey;
    @Value("${tracker.address}")
    private String address;

    public String getAddress() {
        return "0x" + this.address.toLowerCase();
    }
}
