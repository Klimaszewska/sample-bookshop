package com.example.samplebookshop;

import com.example.samplebookshop.order.application.OrderProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@EnableScheduling
@EnableConfigurationProperties(OrderProperties.class)
@SpringBootApplication
public class SampleBookshopApplication {

    public static void main(String[] args) {
        SpringApplication.run(SampleBookshopApplication.class, args);
    }

}
