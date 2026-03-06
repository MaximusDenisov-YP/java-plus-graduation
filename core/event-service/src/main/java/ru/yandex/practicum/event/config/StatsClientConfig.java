package ru.yandex.practicum.event.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import ru.yandex.practicum.ewm.stats.client.StatisticsService;
import ru.yandex.practicum.ewm.stats.client.StatisticsServiceImpl;

@Configuration
@Slf4j
public class StatsClientConfig {

    @Value("${stats-server.service-id:STATS-SERVER}")
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