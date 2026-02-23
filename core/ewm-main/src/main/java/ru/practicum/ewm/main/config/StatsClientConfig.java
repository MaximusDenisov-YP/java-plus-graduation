package ru.practicum.ewm.main.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import ru.practicum.ewm.stats.client.StatisticsService;
import ru.practicum.ewm.stats.client.StatisticsServiceImpl;

@Configuration
@Slf4j
public class StatsClientConfig {

    @Value("${stats-server.service-id:EWM-STATS-SERVICE}")
    private String statsServerId;

    @Bean
    public StatisticsService statisticsService(RestTemplate restTemplate) {
        String statsUrl = "http://" + statsServerId;
        return new StatisticsServiceImpl(restTemplate, statsUrl);
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}