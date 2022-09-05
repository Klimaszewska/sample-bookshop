package com.example.samplebookshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SampleBookshopApplication {

    public static void main(String[] args) {
        SpringApplication.run(SampleBookshopApplication.class, args);
    }

}
