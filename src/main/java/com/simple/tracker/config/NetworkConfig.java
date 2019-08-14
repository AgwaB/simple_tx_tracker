package com.simple.tracker.config;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;
import org.web3j.protocol.Web3j;
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
        if(initConfig.getMode().equals("ropsten")) {
            host = initConfig.getRopstenUrl() + initConfig.getRopstenKey();
        }

        if(initConfig.getMode().equals("ganache")) {
            host = initConfig.getGanacheUrl() + ":" + initConfig.getGanachePort();
        }
    }

    @Bean
    public Web3j web3j() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
//                .addInterceptor(interceptor)
                .build();
        HttpService httpService = new HttpService(host, client, true);
        return Web3j.build(httpService);
    }


}
