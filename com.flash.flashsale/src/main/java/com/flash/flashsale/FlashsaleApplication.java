package com.flash.flashsale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableFeignClients
@SpringBootApplication
@ComponentScan(basePackages = {"com.flash.base", "com.flash.flashsale"})
public class FlashsaleApplication {
    public static void main(String[] args) {
        SpringApplication.run(FlashsaleApplication.class, args);
    }
}