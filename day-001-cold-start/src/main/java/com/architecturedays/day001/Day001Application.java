package com.architecturedays.day001;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class Day001Application {

    private static final Logger log = LoggerFactory.getLogger(Day001Application.class);

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        SpringApplication.run(Day001Application.class, args);
        long startupTime = System.currentTimeMillis() - startTime;
        log.info("Aplicacion iniciada en {} ms", startupTime);
    }
}
