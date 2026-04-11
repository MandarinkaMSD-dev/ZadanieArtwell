package com.example.zadanieslave;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


@SpringBootApplication(scanBasePackages = "com.example.zadanieslave")
@EnableAsync
@EnableAspectJAutoProxy
public class ZadanieslaveApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZadanieslaveApplication.class, args);
    }

}
