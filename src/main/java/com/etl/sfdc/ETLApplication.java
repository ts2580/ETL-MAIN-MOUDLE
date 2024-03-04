package com.etl.sfdc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class ETLApplication {

    public static void main(String[] args) {
        SpringApplication.run(ETLApplication.class, args);
    }

}
