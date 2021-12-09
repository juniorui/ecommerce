package com.sigga.ecommerce.crm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.sigga.ecommerce")
public class CrmApplication {

    public static void main(String[] args) {

        SpringApplication.run(CrmApplication.class, args);
    }
}
