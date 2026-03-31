package com.dk.salesystem;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.dk.salesystem.mapper")
public class SaleSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(SaleSystemApplication.class, args);
    }
}
