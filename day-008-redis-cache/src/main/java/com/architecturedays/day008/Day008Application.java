package com.architecturedays.day008;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class Day008Application {

    public static void main(String[] args) {
        SpringApplication.run(Day008Application.class, args);
    }
}
