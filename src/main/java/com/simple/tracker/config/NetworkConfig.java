package com.simple.tracker.config;

import com.simple.tracker.app.parity.ParityService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.http.HttpService;

import javax.annotation.PostConstruct;
import java.util.Collections;

@Configuration
public class NetworkConfig {
    @Autowired
    private InitConfig initConfig;
    private String host;

    @PostConstruct
    public void prepare() {
        if(initConfig.getMode().equals("parity")) {
            host = parityBaseUrl();
        }

        if(initConfig.getMode().equals("infura")) {
            host = infuraBaseUrl();
        }

        if(initConfig.getMode().equals("ganache")) {
            host = ganacheBaseUrl();
        }
    }

    @Bean
    public Admin web3j() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
//                .addInterceptor(interceptor)
                .build();
        HttpService httpService = new HttpService(host, client, true);
        return Admin.build(httpService);
    }

    @Bean("parityBaseUrl")
    public String parityBaseUrl() {
        return initConfig.getParityUrl() + ":" + initConfig.getParityPort();
    }

    @Bean("infuraBaseUrl")
    public String infuraBaseUrl() {
        return initConfig.getInfuraUrl() + "/" + initConfig.getInfuraKey();
    }

    @Bean("ganacheBaseUrl")
    public String ganacheBaseUrl() {
        return initConfig.getGanacheUrl() + ":" + initConfig.getGanachePort();
    }

    @Bean
    public RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }

    @Bean(name = "parityHttpHeaders")
    public HttpHeaders parityHttpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", "application/json");
        return httpHeaders;
    }
}
