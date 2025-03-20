package com.pca.pokimages;

// src/main/java/com/example/pokimages/PokimagesApplication.java

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class PokimagesApplication {
    public static void main(String[] args) {
        SpringApplication.run(PokimagesApplication.class, args);
    }
}