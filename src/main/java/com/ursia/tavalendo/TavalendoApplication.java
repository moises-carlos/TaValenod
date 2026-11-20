package com.ursia.tavalendo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TavalendoApplication {

    public static void main(String[] args) {
        SpringApplication.run(TavalendoApplication.class, args);
    }
}