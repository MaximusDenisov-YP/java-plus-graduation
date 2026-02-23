package ru.practicum.ewm.main.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import ru.practicum.ewm.stats.client.StatisticsService;
import ru.practicum.ewm.stats.client.StatisticsServiceImpl;

@Configuration
public class StatsClientConfig {

    @Value("${stats-server.url}")
    private String statsServerUrl;

    @Bean
    public StatisticsService statisticsService(RestTemplate restTemplate) {
        return new StatisticsServiceImpl(restTemplate, statsServerUrl);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}