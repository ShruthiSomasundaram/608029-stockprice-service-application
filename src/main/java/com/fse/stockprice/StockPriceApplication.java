package com.fse.stockprice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class StockPriceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockPriceApplication.class, args);
    }
}
