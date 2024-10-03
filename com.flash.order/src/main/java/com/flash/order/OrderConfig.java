package com.flash.order;

import com.siot.IamportRestClient.IamportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderConfig {
    @Value("${iamport.apiKey}")
    String apiKey;

    @Value("${iamport.apiSecret}")
    String apiSecret = "API_SECRET";

    @Bean
    public IamportClient iamportClient() {
        return new IamportClient(apiKey, apiSecret);
    }
}
