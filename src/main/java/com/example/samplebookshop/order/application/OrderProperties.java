package com.example.samplebookshop.order.application;

import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.time.Duration;

@Value
@ConstructorBinding
@ConfigurationProperties("app.order")
public class OrderProperties {
    String abandonCron;
    Duration paymentPeriod;
}
