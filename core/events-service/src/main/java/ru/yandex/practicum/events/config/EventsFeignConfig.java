package ru.yandex.practicum.events.config;

import feign.Logger;
import feign.Request;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventsFeignConfig {

    @Bean
    public Request.Options requestOptions() {
        return new Request.Options(
                3000,  // connect timeout
                5000   // read timeout
        );
    }

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }
}