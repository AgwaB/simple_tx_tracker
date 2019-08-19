package com.simple.tracker.test;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Component
public class config {
    @Bean
    public Web3j web3j() {
        return Web3j.build(new HttpService("https://ropsten.infura.io/v3/4feb1e5949df453fa6326d4328fe5b80"));
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
