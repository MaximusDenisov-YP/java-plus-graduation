package ru.yandex.practicum.requestservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.statsclient.client.config.StatsClientConfig;

@EnableFeignClients(basePackages = {"ru.yandex.practicum.requestservice.fallback"})
@SpringBootApplication
@Import(StatsClientConfig.class)
public class RequestsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RequestsServiceApplication.class, args);
	}

}
