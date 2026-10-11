package com.dimas.payments;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class PaymentOrdersApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentOrdersApplication.class, args);
    }
}
