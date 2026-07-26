package com.clauseguard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ClauseGuardApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClauseGuardApplication.class, args);
    }
}
